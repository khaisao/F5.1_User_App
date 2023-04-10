package jp.careapp.counseling.android.ui.review_mode.live_stream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.android.ui.live_stream.LiveStreamActionState
import jp.careapp.counseling.android.ui.live_stream.LiveStreamMode
import jp.careapp.counseling.android.ui.live_stream.LiveStreamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMLiveStreamViewModel @Inject constructor(
    private val mRepository: LiveStreamRepository
) : BaseViewModel() {

    private val _performerName = MutableLiveData<String>()
    val performerName: LiveData<String>
        get() = _performerName

    private var currentMode = LiveStreamMode.PARTY
    private val _currentModeLiveData = MutableLiveData(currentMode)
    val currentModeLiveData: LiveData<Int>
        get() = _currentModeLiveData

    private val _commentList = MutableLiveData<ArrayList<LiveStreamChatResponse>>()
    val commentList: LiveData<ArrayList<LiveStreamChatResponse>>
        get() = _commentList

    val mActionState = SingleLiveEvent<LiveStreamActionState>()

    init {
        loadComment()
    }

    fun changeMode(mode: Int) {
        currentMode = mode
        _currentModeLiveData.value = currentMode
    }

    fun handleMicAndCamera() {
        mActionState.value = LiveStreamActionState.OpenBottomSheetSettingCameraAndMic
    }

    fun reloadMode() {
        _currentModeLiveData.value = currentMode
    }

    private fun loadComment() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val abc = arrayListOf<LiveStreamChatResponse>()
                    abc.add(LiveStreamChatResponse(
                        id = 2,
                        isPerformer = true,
                        isWhisper = false,
                        name = "Hai Dang Le Vu",
                        message = "Message"
                    ))
                    abc.add(LiveStreamChatResponse(
                        id = 2,
                        isPerformer = true,
                        isWhisper = false,
                        name = "Hai Dang Le Vu",
                        message = "Message"
                    ))
                    withContext(Dispatchers.Main) {
                        _commentList.value = abc
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

    fun sendComment(comment: String) {
//        isLoading.value = true
//        viewModelScope.launch(Dispatchers.IO) {
//            supervisorScope {
//                try {
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    withContext(Dispatchers.Main) {
//                        isLoading.value = false
//                    }
//                }
//            }
//        }
    }
}
