package jp.slapp.android.android.ui.verificationCodeHelp

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
class VerificationCodeHelpViewModel @Inject constructor(
    private val mRepository: VerifyCodeHelpRepository
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<VerifyCodeHelpActionState>()

    fun resendOtp(mail: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.resendOtp(mail)
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mActionState.value = VerifyCodeHelpActionState.ResendOtpSuccess
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

sealed class VerifyCodeHelpActionState {
    object ResendOtpSuccess : VerifyCodeHelpActionState()
}
