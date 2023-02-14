package jp.careapp.counseling.android.ui.reviewConsultant

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

open class ReviewConsultantViewModel @ViewModelInject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {

    var isSuccess = MutableLiveData(false)
    var showRating = MutableLiveData(false)
    var reviewResponse = MutableLiveData<Int>()
    open var consultantCode = ""
    val handler = CoroutineExceptionHandler { _, exception ->
        isLoading.value = false
    }

    open fun submitReview(point: Int, review: String) {
        viewModelScope.launch(handler) {
            isLoading.value = true
            val result: ApiObjectResponse<Any> =
                apiInterface.submitReview(consultantCode, point, review)
            result.let {
                if (it.errors.isNullOrEmpty()) {
                    reviewResponse.value = point
                }
            }
            isLoading.value = false
        }
    }
}
