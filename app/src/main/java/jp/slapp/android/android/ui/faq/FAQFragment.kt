package jp.slapp.android.android.ui.faq

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.BuildConfig
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.CONTACT_US_MODE
import jp.slapp.android.android.utils.ContactUsMode
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentFaqBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FAQFragment : BaseFragment<FragmentFaqBinding, FAQViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_faq

    private val mViewModel: FAQViewModel by viewModels()
    override fun getVM() = mViewModel

    companion object {
        private const val FAQ_URL = BuildConfig.WS_ORIGIN + "/question"
        private const val INQUIRY_URL = BuildConfig.WS_ORIGIN + "/inquiry"
        private const val UNSUBSCRIBE_URL = BuildConfig.WS_ORIGIN + "/unsubscribe"
    }

    override fun initView() {
        super.initView()
        configWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView() {
        binding.webView.apply {
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    startLoading()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    endLoading()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    try {
                        view?.loadUrl(url)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    return true
                }
            }
            settings.javaScriptEnabled = true
            clearCache(true)
            loadUrl(FAQ_URL)
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (view.url != null) {
                    if (view.url == INQUIRY_URL) {
                        appNavigation.openFAQToWithdrawal()
                        binding.webView.loadUrl(FAQ_URL)
                    }
                    if (view.url == UNSUBSCRIBE_URL) {
                        appNavigation.openContactUs(
                            bundleOf(
                                CONTACT_US_MODE to ContactUsMode.CONTACT_WITHOUT_MAIL
                            )
                        )
                        binding.webView.loadUrl(FAQ_URL)
                    }
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    private fun startLoading() {
        showHideLoading(true)
    }

    private fun endLoading() {
        showHideLoading(false)
    }

}