package jp.careapp.counseling.android.ui.live_stream

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseDialogFragment
import jp.careapp.counseling.BuildConfig
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentLiveStreamBuyPointBinding
import timber.log.Timber

@AndroidEntryPoint
class LiveStreamBuyPointFragment : BaseDialogFragment<FragmentLiveStreamBuyPointBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_live_stream_buy_point

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
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
                    webViewClient = getBuyPointWebViewClient()
                    settings.javaScriptEnabled = true
                    webChromeClient = WebChromeClient()
                    clearCache(true)
                    loadUrl(urlWebView)
                }
            }
        }

        handleBackPress()
    }

    private fun startLoading() {
        binding.progressBar.visibility = VISIBLE
    }

    private fun endLoading() {
        binding.progressBar.visibility = GONE
    }

    private fun getBuyPointWebViewClient(): WebViewClient {
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
                            dismiss()
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
                return true
            }
        }
    }

    private fun handleBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {
        fun newInstance(webUrl: String) = LiveStreamBuyPointFragment().apply {
            arguments = Bundle().also {
                it.putString(Define.URL_WEB_VIEW, webUrl)
            }
        }
    }
}
