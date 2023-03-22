package jp.careapp.counseling.android.ui.live_stream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val mRepository: LiveStreamRepository
) : BaseViewModel() {

    private val _currentMode = MutableLiveData(LiveStreamMode.PARTY)
    val currentMode: LiveData<Int>
        get() = _currentMode

    val mActionState = SingleLiveEvent<LiveStreamActionState>()

    fun changeToPartyMode() {
        _currentMode.value = LiveStreamMode.PARTY
    }

    fun changeToPrivateMode() {
        _currentMode.value = LiveStreamMode.PRIVATE
    }

    fun changeToPremiumPrivateMode() {
        _currentMode.value = LiveStreamMode.PREMIUM_PRIVATE
    }

    fun handleMicAndCamera() {
        if (currentMode.value == LiveStreamMode.PRIVATE) {
            mActionState.value = LiveStreamActionState.ChangeToPremiumPrivateFromPrivate
        } else {
            mActionState.value = LiveStreamActionState.OpenBottomSheetSettingCameraAndMic
        }
    }
}

sealed class LiveStreamActionState {
    object ChangeToPremiumPrivateFromPrivate : LiveStreamActionState()
    object OpenBottomSheetSettingCameraAndMic : LiveStreamActionState()
}