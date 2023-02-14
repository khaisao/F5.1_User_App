package jp.careapp.counseling.android.ui.webView

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
import jp.careapp.counseling.android.data.network.BlogResponse
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

    override fun initView() {
        super.initView()
        if (BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true)
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
                    webChromeClient = WebChromeClient()
                    clearCache(true)
                    loadUrl(urlWebView)
                }
            }

            if (it.containsKey(Define.BLOG_ID)) {
                val blogId = it.getString(Define.BLOG_ID).toString()
                viewModel.getBlog(blogId)
            }
        }
        binding.webView.setBackgroundColor(resources.getColor(R.color.color_background_common))
        handleBackPress()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.blogResult.observe(viewLifecycleOwner) {
            it?.let {
                initBlog(it)
            }
        }
    }

    private fun initBlog(blog: BlogResponse) {
        binding.toolBar.setTvTitle(blog.title)
        binding.webView.apply {
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            setBackgroundColor(Color.WHITE)
            webChromeClient = WebChromeClient()
            webViewClient = getWebViewClient("")
            clearCache(true)
            loadData(blog.content.toString(), "text/html", "UTF-8")
        }
    }

    private fun loadJs(webview: WebView, script: String) {
        webview.loadUrl(
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
                url: String
            ): Boolean {
                try {
                    if (URLUtil.isNetworkUrl(url)) {
                        view.loadUrl(url)
                    } else if (Define.CALL_BACK_BUY_POINT_GOOGLE_ == url) {
                        appNavigation.openWebBuyPointToBuyPointGoogle()
                    } else if (Define.CALL_BACK_BUY_POINT_PAYPAY == url) {
                        appNavigation.navController?.let {
                            it.popBackStack()
                            showBuyPointSuccessDialog(R.string.buy_point_suscess)
                        }
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
