package jp.careapp.counseling.android.ui.labo.report

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber

const val TAG = "ReportLaboViewModel"

class ReportLaboViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    private val _actionState = SingleLiveEvent<ActionState>()
    val actionState: SingleLiveEvent<ActionState> = _actionState
    private var labId: Int = 0

    fun setLabId(id: Int) {
        labId = id
    }

    fun reportLabo(reason: String) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                apiInterface.reportLabo(labId, reason).apply {
                    if (errors.isEmpty()) {
                        _actionState.postValue(ActionState.ReportLaboSuccess)
                    } else {
                        messageError.postValue(errors[0])
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e)
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

sealed class ActionState {
    object ReportLaboSuccess : ActionState()
}