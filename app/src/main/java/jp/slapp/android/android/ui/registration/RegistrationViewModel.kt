package jp.slapp.android.android.ui.registration

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.utils.MODE_USER
import jp.slapp.android.android.utils.appsflyer.AppsFlyerUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val mRepository: RegistrationRepository,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<RegistrationActionState>()

    private fun setReceiveMail(receiveMail: Boolean): Int {
        if (receiveMail) return 1
        return 0
    }

    fun register(userName: String, receiveMail: Boolean, tokenInput: String, email: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val registerResponse =
                        mRepository.register(userName, setReceiveMail(receiveMail), tokenInput)
                    if (registerResponse.errors.isEmpty()) {
                        val dataResponse = registerResponse.dataResponse
                        val token = dataResponse.token.toString()
                        val tokenExpire = dataResponse.tokenExpire.toString()
                        val password = dataResponse.password.toString()
                        val memberCode = dataResponse.memberCode.toString()
                        mRepository.saveUserInfo(token, tokenExpire, password, memberCode)
                        mRepository.saveMemberName(userName)
                        rxPreferences.saveEmail(email)
                        mRepository.setFirstRegister()
                        AppsFlyerUtil.handleEventRegistration(memberCode)
                        val getMemberInfoResponse = mRepository.getMemberInfo()
                        if (getMemberInfoResponse.errors.isEmpty()) {
                            val dataGetMemberInfoResponse =
                                getMemberInfoResponse.dataResponse
                            withContext(Dispatchers.Main) {
                                if (dataGetMemberInfoResponse.disPlay == MODE_USER.MEMBER_APP_REVIEW_MODE) {
                                    mActionState.value = RegistrationActionState.NavigateToTopRM
                                } else {
                                    mActionState.value = RegistrationActionState.NavigateToTop
                                }
                            }
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
    object NavigateToTop : RegistrationActionState()
    object NavigateToTopRM : RegistrationActionState()
}
