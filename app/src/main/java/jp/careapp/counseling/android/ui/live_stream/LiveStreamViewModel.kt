package jp.careapp.counseling.android.ui.live_stream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val mRepository: LiveStreamRepository
) : BaseViewModel() {

    private val _performerName = MutableLiveData<String>()
    val performerName: LiveData<String>
        get() = _performerName

    private var currentMode = LiveStreamMode.PARTY
    private val _currentModeLiveData = MutableLiveData(currentMode)
    val currentModeLiveData: LiveData<Int>
        get() = _currentModeLiveData

    val mActionState = SingleLiveEvent<LiveStreamActionState>()

    fun changeMode(mode: Int) {
        currentMode = mode
        _currentModeLiveData.value = currentMode
    }

    fun handleMicAndCamera() {
        if (currentModeLiveData.value == LiveStreamMode.PRIVATE) {
            mActionState.value = LiveStreamActionState.ChangeToPremiumPrivateFromPrivate
        } else {
            mActionState.value = LiveStreamActionState.OpenBottomSheetSettingCameraAndMic
        }
    }

    fun reloadMode() {
        _currentModeLiveData.value = currentMode
    }

    fun sendComment(comment: String) {
//        isLoading.value = true
//        viewModelScope.launch(Dispatchers.IO) {
//            supervisorScope {
//                try {
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    isLoading.value = false
//                }
//            }
//        }
    }
}

sealed class LiveStreamActionState {
    object ChangeToPremiumPrivateFromPrivate : LiveStreamActionState()
    object OpenBottomSheetSettingCameraAndMic : LiveStreamActionState()
}