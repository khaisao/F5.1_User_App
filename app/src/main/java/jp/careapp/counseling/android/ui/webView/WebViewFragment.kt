package jp.careapp.counseling.android.ui.webView

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.BuildConfig
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.user_profile.ActionLoadProfile
import jp.careapp.counseling.android.data.pref.AppPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.Define.Companion.URL_FREE_POINT
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentWebViewBinding
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WebViewFragment : BaseFragment<FragmentWebViewBinding, WebViewViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var preferences: AppPreferences
    override val layoutId = R.layout.fragment_web_view
    private val viewModel: WebViewViewModel by viewModels()
    override fun getVM() = viewModel

    private lateinit var webViewTitle:  Map<String, String>

    override fun initView() {
        super.initView()
        if (BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true)

        webViewTitle = mapOf(
            "/payment/methods" to requireContext().getString(R.string.methods_title),
            "/payment/buy-point" to requireContext().getString(R.string.buy_point_title),
            "/payment/bank" to requireContext().getString(R.string.bank_title),
            "/payment/credit" to requireContext().getString(R.string.credit_title),
            "/payment/credit-first" to requireContext().getString(R.string.credit_first_title),
            "/payment/credit-result-success" to requireContext().getString(R.string.credit_result_success),
            "/payment/credit-result-fail" to requireContext().getString(R.string.credit_result_fail),
            "/payment/recharge-bank" to requireContext().getString(R.string.recharge_bank_title),
            "/payment/recharge-bank-success" to requireContext().getString(R.string.recharge_bank_success_title),
            "/payment/auto-charge" to requireContext().getString(R.string.auto_charge_title),
            "/payment/service-act" to requireContext().getString(R.string.service_act_title)
        )

        configWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView() {
        requireArguments().let {
            if (it.containsKey(Define.TITLE_WEB_VIEW)) {
                binding.toolBar.setTvTitle(it.getString(Define.TITLE_WEB_VIEW).toString())
            }

            if (it.containsKey(Define.URL_WEB_VIEW)) {
                val urlWebView = it.getString(Define.URL_WEB_VIEW).toString()
                binding.webView.apply {
                    settings.domStorageEnabled = true
                    webViewClient = getWebViewClient(urlWebView)
                    settings.javaScriptEnabled = true
                    clearCache(true)
                    loadUrl(urlWebView)
                }

                binding.webView.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        if (view.url != null) {
                            setTitleByUrl(view.url!!)
                        }
                    }
                }
            }
        }

        handleBackPress()
    }

    private fun loadJs(webView: WebView, script: String) {
        webView.loadUrl(
            "javascript:" +
                    "function addScriptTag() { " +
                    "\n  var headTag = document.getElementsByTagName(\"head\")[0]; " +
                    "\n  var scriptTag = document.createElement(\"script\"); " +
                    "\n  var content = document.createTextNode('$script');  " +
                    "\n  scriptTag.appendChild(content); " +
                    "\n  headTag.appendChild(scriptTag); " +
                    " \n}" +
                    "\n addScriptTag();" +
                    ""
        )
    }

    private fun setTitleByUrl(urlWebView: String) {
        try {

            val path = urlWebView.substringAfter(BuildConfig.WEB_DOMAIN).substringBefore("?")
                .substring(0)
            val title = webViewTitle[path]
            if (title != "") {
                binding.toolBar.setTvTitle(title)
            } else {
                binding.toolBar.setTvTitle(requireContext().getString(R.string.methods_title))
            }
        } catch (_: Exception) {
        }
    }

    private fun startLoading() {
        showHideLoading(true)
    }

    private fun endLoading() {
        showHideLoading(false)
    }

    private fun getWebViewClient(urlWebView: String): WebViewClient {
        val token = try {
            preferences.getToken()?.substring(7)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        val env = if (BuildConfig.BUILD_TYPE == "release") "PRODUCTION" else "STAGING"
        val scriptFreePoint =
            "var token=\"${token}\"; var domain = \"${BuildConfig.BASE_URL}\"; var env = \"${env}\"; handleFreePointView();"
        val scriptBuyPoint =
            "var token=\"${token}\"; var domain = \"${BuildConfig.BASE_URL}\"; var env = \"${env}\"; handlePointInfo(); var store = \"googleplay\""
        return object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                startLoading()
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                endLoading()
                if (urlWebView == URL_FREE_POINT)
                    loadJs(view, scriptFreePoint)
                else
                    loadJs(view, scriptBuyPoint)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                try {
                    if (URLUtil.isNetworkUrl(url)) {
                        view.loadUrl(url)
                    } else if (Define.CALL_BACK_BUY_POINT_GOOGLE_ == url) {
                        appNavigation.openWebBuyPointToBuyPointGoogle()
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
                return true
            }
        }
    }

    private fun showBuyPointSuccessDialog(@StringRes title: Int) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(title)
            .setTextPositiveButton(jp.careapp.core.R.string.text_OK)
            .setOnPositivePressed {
                it.dismiss()
            }
    }

    private inner class Client : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            startLoading()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            endLoading()
        }

        override
        fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    if (binding.webView.canGoBack()) {
                        startLoading()
                        binding.webView.goBack()
                    } else {
                        appNavigation.navigateUp()
                    }
                }

                override fun onClickRight() {
                    super.onClickRight()
                    appNavigation.navController?.let {
                        notifyUpdateIfNeed()
                        it.popBackStack()
                    }
                }
            }
        )
    }

    private fun handleBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    notifyUpdateIfNeed()
                    appNavigation.navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun notifyUpdateIfNeed() {
        requireArguments().let { bundle ->
            if (bundle.containsKey(Define.URL_WEB_VIEW)) {
                bundle.getString(Define.URL_WEB_VIEW).toString().let { url ->
                    if (url.contains(Define.URL_BUY_POINT_CREDIT_CARD_CONFIRM_SEGMENT) ||
                        url.contains(Define.URL_BUY_POINT_CREDIT_CARD_ONECLICK_CONFIRM_SEGMENT)
                    ) {
                        EventBus.getDefault().post(ActionLoadProfile(true))
                    }
                }
            }
        }
    }
}
