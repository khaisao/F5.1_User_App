package jp.careapp.counseling.android.ui.webView

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.BlogResponse
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber

class WebViewViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    private val _blogResult = MutableLiveData<BlogResponse>()
    val blogResult: MutableLiveData<BlogResponse> = _blogResult

    fun getBlog(blogId: String) {
        viewModelScope.launch {
            try {
                apiInterface.getBlogs(blogId).dataResponse.let {
                    _blogResult.postValue(it)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
