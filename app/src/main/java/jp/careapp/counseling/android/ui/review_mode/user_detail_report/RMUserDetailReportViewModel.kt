package jp.careapp.counseling.android.ui.review_mode.user_detail_report

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMUserDetailReportViewModel @ViewModelInject constructor(
    private val rmApiInterface: RMApiInterface,
    @Assisted
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val actionState = SingleLiveEvent<ActionState>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    var userCode: String = ""

    init {
        userCode = savedStateHandle.get<String>(BUNDLE_KEY.PERFORMER_CODE).toString()
    }

    fun sendReportUser(content: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = rmApiInterface.sendReport(userCode, content)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    actionState.value = ActionState.SendReportSuccess(true)
                } else {
                    actionState.value = ActionState.SendReportSuccess(false)
                }
            }
        }
    }
}