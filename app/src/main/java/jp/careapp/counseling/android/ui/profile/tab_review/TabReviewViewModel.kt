package jp.careapp.counseling.android.ui.profile.tab_review

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.model.user_profile.ReviewUserProfile
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch

class TabReviewViewModel @ViewModelInject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {
    val userProfileResult = MutableLiveData<List<ReviewUserProfile>?>()

    fun loadDetailUser(code: String) {
        viewModelScope.launch {
            try {
                val response = apiInterface.getReviewUserProfile(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        userProfileResult.value = it.dataResponse
                    }
                }
            } catch (throwable: Throwable) {
            }
        }
    }
}
