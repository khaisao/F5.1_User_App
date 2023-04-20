package jp.careapp.counseling.android.ui.live_stream

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
import jp.careapp.counseling.databinding.FragmentLiveStreamBuyPointBinding
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LiveStreamBuyPointFragment :
    BaseFragment<FragmentLiveStreamBuyPointBinding, LiveStreamBuyPointViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var preferences: AppPreferences
    override val layoutId = R.layout.fragment_live_stream_buy_point
    private val viewModel: LiveStreamBuyPointViewModel by viewModels()
    override fun getVM() = viewModel

    override fun initView() {
        super.initView()
        if (BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true)
        configWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView() {
        requireArguments().let {
            if (it.containsKey(Define.URL_WEB_VIEW)) {
                val urlWebView = it.getString(Define.URL_WEB_VIEW).toString()
                binding.webView.apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    setBackgroundResource(0)
                    settings.domStorageEnabled = true
                    webViewClient = getWebViewClient(urlWebView)
                    settings.javaScriptEnabled = true
                    webChromeClient = WebChromeClient()
                    clearCache(true)
                    loadUrl(urlWebView)
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
                loadJs(view, scriptBuyPoint)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                try {
                    when {
                        URLUtil.isNetworkUrl(url) -> {
                            view.loadUrl(url)
                        }
                        Define.CALL_BACK_BUY_POINT_CREDIT_CLOSE == url -> {
                            appNavigation.navigateUp()
                        }
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
