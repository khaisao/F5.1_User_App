package jp.careapp.counseling.android.ui.review_mode.my_menu

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.ActionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMMyMenuViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface
) : BaseViewModel() {
    val actionState = SingleLiveEvent<ActionState>()

    fun handleWithdrawal(reason: String) {
        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = rmApiInterface.withdrawal(reason)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (response.errors.isEmpty()) {
                        actionState.value = ActionState.WithdrawalSuccess(true)
                    } else {
                        actionState.value = ActionState.WithdrawalSuccess(false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading.value = false
        }
    }
}