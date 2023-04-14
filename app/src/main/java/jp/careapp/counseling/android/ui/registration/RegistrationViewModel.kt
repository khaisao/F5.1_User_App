package jp.careapp.counseling.android.ui.registration

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val mRepository: RegistrationRepository
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<RegistrationActionState>()

    private fun setReceiveMail(receiveMail: Boolean): Int {
        if (receiveMail) return 1
        return 0
    }

    fun register(userName: String, receiveMail: Boolean) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.register(userName, setReceiveMail(receiveMail))
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        val token = dataResponse.token.toString()
                        val tokenExpire = dataResponse.tokenExpire.toString()
                        val password = dataResponse.password.toString()
                        val memberCode = dataResponse.memberCode.toString()
                        mRepository.saveUserInfo(token, tokenExpire, password, memberCode)
                        mRepository.saveMemberName(userName)
                        mRepository.setFirstRegister()
                        withContext(Dispatchers.Main) {
                            mActionState.value = RegistrationActionState.SetNickNameSuccess
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }
}

sealed class RegistrationActionState {
    object SetNickNameSuccess : RegistrationActionState()
}
