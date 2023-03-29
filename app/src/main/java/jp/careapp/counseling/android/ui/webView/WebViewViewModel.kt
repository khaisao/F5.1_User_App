package jp.careapp.counseling.android.ui.webView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.BlogResponse
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
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
