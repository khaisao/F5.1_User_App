package jp.careapp.counseling.android.ui.review_mode.setting_push

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.ui.review_mode.setting_push.RMSettingPushFragment.Companion.PUSH_DO_NOT_RECEIVE
import jp.careapp.counseling.android.ui.review_mode.setting_push.RMSettingPushFragment.Companion.PUSH_RECEIVE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMSettingPushViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<RMSettingPushActionState>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    private var _pushMail = MutableLiveData<Int>()
    var pushMail: LiveData<Int> = _pushMail

    init {
        val pushMailLocal = rxPreferences.getPushMail()
        _pushMail.value = pushMailLocal
    }

    fun updateSettingPush(isChecked: Boolean) {
        isLoading.value = true
        val pushMailRequest = if (isChecked) PUSH_RECEIVE else PUSH_DO_NOT_RECEIVE
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = rmApiInterface.updateSettingPush(pushMailRequest)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                val isError = response.errors.isEmpty()
                if (isError) rxPreferences.setPushMail(pushMailRequest)
                updateStateSwitch(isSuccess = isError, pushMail = _pushMail.value)
            }
        }
    }

    private fun updateStateSwitch(isSuccess: Boolean, pushMail: Int?) {
        if (isSuccess) {
            mActionState.value = RMSettingPushActionState.SettingPushSuccess
            _pushMail.value = if (pushMail == PUSH_RECEIVE) PUSH_DO_NOT_RECEIVE else PUSH_RECEIVE
        } else {
            _pushMail.value = _pushMail.value
        }
    }
}

sealed class RMSettingPushActionState {
    object SettingPushSuccess : RMSettingPushActionState()
}