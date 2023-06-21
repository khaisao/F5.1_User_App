package jp.slapp.android.android.ui.withdrawal

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel @Inject constructor(
    private val mRepository: WithdrawalRepository
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<WithdrawalActionState>()

    fun handleWithdrawal(reason: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.withdrawal(reason)
                    if (response.errors.isEmpty()) {
                        mRepository.clearAppPreferences()
                        withContext(Dispatchers.Main) {
                            mActionState.value = WithdrawalActionState.WithdrawalSuccess
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        }
    }
}

sealed class WithdrawalActionState {
    object WithdrawalSuccess : WithdrawalActionState()
}
