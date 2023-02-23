package jp.careapp.counseling.android.ui.review_mode.enterName

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMEnterNameViewModel @ViewModelInject constructor(
    private val mRepository: RMEnterNameRepository
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<RMEnterNameActionState>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun submitNickName(nickName: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = mRepository.setNickNameRM(nickName)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    mActionState.value = RMEnterNameActionState.SetNickNameSuccess(true)
                    mRepository.saveUserInfoRM(
                        response.dataResponse.memberCode,
                        response.dataResponse.email,
                        response.dataResponse.password,
                        response.dataResponse.token,
                        response.dataResponse.tokenExpire
                    )
                } else {
                    mActionState.value = RMEnterNameActionState.SetNickNameSuccess(false)
                }
            }
        }
    }
}

sealed class RMEnterNameActionState {
    class SetNickNameSuccess(val isSuccess: Boolean) : RMEnterNameActionState()
}