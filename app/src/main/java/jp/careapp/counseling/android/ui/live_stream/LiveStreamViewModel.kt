package jp.careapp.counseling.android.ui.live_stream

import android.app.Activity
import android.app.Application
import android.media.AudioManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.live_stream.ConnectResult
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.socket.CallingWebSocketClient
import jp.careapp.counseling.android.network.socket.FlaxWebSocketManager
import jp.careapp.counseling.android.network.socket.MaruCastManager
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.PERFORMER
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.POINT
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_ASK_TWO_SHOT
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CANCEL_TWO_SHOT
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CHANGE_CHAT_STATUS
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CHAT_LOG
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_MESSAGE
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_RELOAD
import jp.careapp.counseling.android.utils.SocketInfo.KEY_ACTION
import jp.careapp.counseling.android.utils.SocketInfo.KEY_CHAT
import jp.careapp.counseling.android.utils.SocketInfo.KEY_ERROR
import jp.careapp.counseling.android.utils.SocketInfo.KEY_HANDLE
import jp.careapp.counseling.android.utils.SocketInfo.KEY_RESULT
import jp.careapp.counseling.android.utils.SocketInfo.KEY_TWO_SHOT
import jp.careapp.counseling.android.utils.SocketInfo.KEY_TWO_SHOT_CANCEL
import jp.careapp.counseling.android.utils.SocketInfo.KEY_TWO_SHOT_CANCEL_2
import jp.careapp.counseling.android.utils.SocketInfo.KEY_USER_CODE
import jp.careapp.counseling.android.utils.SocketInfo.KEY_USER_TYPE
import jp.careapp.counseling.android.utils.SocketInfo.KEY_WHISPER
import jp.careapp.counseling.android.utils.SocketInfo.RESULT_NG
import jp.careapp.counseling.android.utils.SocketInfo.RESULT_OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val application: Application,
    private val rxPreferences: RxPreferences,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,
    private val audioManager: AudioManager
) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack {

    private val _messageList = MutableLiveData<ArrayList<LiveStreamChatResponse>>()
    val messageList: LiveData<ArrayList<LiveStreamChatResponse>>
        get() = _messageList

    private val _whisperList = MutableLiveData<ArrayList<LiveStreamChatResponse>>()
    val whisperList: LiveData<ArrayList<LiveStreamChatResponse>>
        get() = _whisperList

    private val _connectResult = MutableLiveData<ConnectResult>()
    val connectResult: LiveData<ConnectResult>
        get() = _connectResult

    private val _updateUIMode = MutableLiveData(UI_NORMAL_MODE)
    val updateUIMode: LiveData<Int>
        get() = _updateUIMode
    private val _twoShot = MutableLiveData<String>()
    val twoShot: LiveData<String>
        get() = _twoShot
    private val _viewerStatus = MutableLiveData<Int>()
    val viewerStatus: LiveData<Int>
        get() = _viewerStatus

    private val _pointState = MutableLiveData<PointState>(PointState.StartCheck)
    val pointState: LiveData<PointState>
        get() = _pointState

    private val _currentPoint = MutableLiveData(rxPreferences.getPoint())
    val currentPoint: LiveData<Int>
        get() = _currentPoint

    private var isLogout = false
    private var oldAudioMode = 0
    private var oldSpeakerPhone = false

    private var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null

    private var isMicMute = false
    private var isCameraMute = false

    init {
        configAudio()
        handleSocketCallback()
    }

    fun sendChatMessage(message: String) {
        if (message.isEmpty()) return
        flaxWebSocketManager.sendMessage(message)
        updateChatLog(message, name = rxPreferences.getNickName() ?: "あなた", false)
    }

    fun updateMicSetting(_isMicMute: Boolean = false) {
        if (isMicMute != _isMicMute) {
            viewModelScope.launch(Dispatchers.IO) {
                maruCastManager.setMicOff(_isMicMute)
            }
        }
        isMicMute = _isMicMute
    }

    fun updateCameraSetting(_isCameraMute: Boolean = false) {
        if (isCameraMute != _isCameraMute) {
            viewModelScope.launch(Dispatchers.IO) {
                maruCastManager.setCameraOff(_isCameraMute)
            }
        }
        isCameraMute = _isCameraMute
    }

    fun isMicMute() = isMicMute

    fun isCameraMute() = isCameraMute

    fun setFlaxLoginAuthResponse(response: FlaxLoginAuthResponse?) {
        flaxLoginAuthResponse = response
    }

    fun setViewerStatus(status: Int) {
        _viewerStatus.value = status
    }

    fun updateMode(mode: Int) {
        when (mode) {
            PARTY -> {
                if (_viewerStatus.value == 1) {
                    flaxWebSocketManager.changeStatus()
                } else {
                    flaxWebSocketManager.privateModeDestroy()
                }
            }
            PRIVATE -> {
                flaxWebSocketManager.privateModeInvitation()
                _updateUIMode.postValue(UI_SHOW_WAITING_PRIVATE_MODE)
            }
            PRIVATE_CANCEL -> {
                privateModeCancel()
            }
            PREMIUM_PRIVATE -> {
                maruCastManager.publishStream()
            }
            BUY_POINT -> {
                _updateUIMode.postValue(UI_BUY_POINT)
            }
        }
    }

    fun sendWhisperMessage(whisperMessage: String) {
        flaxWebSocketManager.sendWhisperMessage(whisperMessage)
    }

    fun logout() {
        audioManager.mode = oldAudioMode
        audioManager.isSpeakerphoneOn = oldSpeakerPhone
        maruCastManager.logoutRoom()
        flaxWebSocketManager.flaxLogout()
    }

    fun privateModeCancel() {
        flaxWebSocketManager.privateModeCancel()
    }

    fun handleConnect(activity: Activity, callBack: MaruCastManager.SwitchViewerCallback?) {
        maruCastManager.removeLoginCallBack()
        maruCastManager.setSwitchViewerCallBack(callBack)
        maruCastManager.handleConnected(activity)
    }

    fun setAudioConfig(audioMode: Int, isSpeakerPhoneOn: Boolean) {
        audioManager.mode = audioMode
        audioManager.isSpeakerphoneOn = isSpeakerPhoneOn
    }

    private fun handleSocketCallback() {
        flaxWebSocketManager.setCallback(this)
    }

    private fun configAudio() {
        oldAudioMode = audioManager.mode
        oldSpeakerPhone = audioManager.isSpeakerphoneOn
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true
    }

    private fun updateChatLog(message: String, name: String, isPerformer: Boolean = false) {
        val item = LiveStreamChatResponse(message = message, name = name, isPerformer = isPerformer)
        val listChat = _messageList.value ?: arrayListOf()
        listChat.add(item)
        _messageList.postValue(listChat)
    }

    private fun updateWhisperLog(message: String, handle: String, isPerformer: Boolean) {
        val item =
            LiveStreamChatResponse(
                message = message,
                name = handle,
                isPerformer = isPerformer,
                isWhisper = true
            )
        val listChat = _messageList.value ?: arrayListOf()
        val listWhisper = _whisperList.value ?: arrayListOf()
        listChat.add(item)
        _messageList.postValue(listChat)
        listWhisper.add(item)
        _whisperList.postValue(listWhisper)
    }

    fun endPointChecking() {
        _pointState.postValue(PointState.EndCheck)
    }

    override fun onHandleMessage(jsonMessage: JSONObject) {
        try {
            val result: String = jsonMessage.getString(KEY_RESULT)
            val action: String? =
                if (jsonMessage.has(KEY_ACTION)) jsonMessage.getString(KEY_ACTION) else null
            if (action == null) {
                if (isLogout) return
                maruCastManager.setRoomExists(
                    !(jsonMessage.has(KEY_ERROR) && jsonMessage.getString(
                        KEY_ERROR
                    ).contains("オフライン"))
                )
                _connectResult.postValue(
                    ConnectResult(
                        RESULT_NG,
                        jsonMessage.getString(KEY_ERROR),
                        isLogout = true
                    )
                )
                isLogout = true
            } else if (result == RESULT_NG) {
                val errorMsg =
                    if (jsonMessage.has(KEY_ERROR)) jsonMessage.getString(KEY_ERROR) else "エラーが発生しました"
                when (action) {
                    ACTION_ASK_TWO_SHOT, ACTION_CANCEL_TWO_SHOT -> {
                        _connectResult.postValue(ConnectResult(RESULT_NG, errorMsg))
                        _updateUIMode.postValue(UI_DISMISS_PRIVATE_MODE)
                    }
                    else -> _connectResult.postValue(ConnectResult(RESULT_NG, errorMsg, true))
                }
            } else if (result == RESULT_OK) {
                when (action) {
                    ACTION_RELOAD, ACTION_CHAT_LOG -> try {
                        val point = jsonMessage.getString(POINT).toInt()
                        _currentPoint.postValue(point)
                        if (_pointState.value == PointState.StartCheck && point <= 1000 && point > 500) {
                            _pointState.postValue(PointState.PointUnder1000)
                        } else if (_pointState.value == PointState.PointUnder1000 && point <= 500) {
                            _pointState.postValue(PointState.PointUnder500)
                        }
                        val chatMessageArray: JSONArray = jsonMessage.getJSONArray(KEY_CHAT)
                        run {
                            var i = 0
                            while (i < chatMessageArray.length()) {
                                val chatMessage = chatMessageArray.getJSONObject(i)
                                val handleName = chatMessage.getString(KEY_HANDLE)
                                val messageStr = chatMessage.getString(ACTION_MESSAGE)
                                val userCode = chatMessage.getString(KEY_USER_CODE)
                                if (userCode != flaxLoginAuthResponse?.memberCode) {
                                    val isPerformer =
                                        chatMessage.getString(KEY_USER_TYPE) == PERFORMER
                                    updateChatLog(messageStr, handleName, isPerformer)
                                }
                                i++
                            }
                        }

                        val whisperMessageArray: JSONArray = jsonMessage.getJSONArray(KEY_WHISPER)
                        var i = 0
                        while (i < whisperMessageArray.length()) {
                            val whisperMessage = whisperMessageArray.getJSONObject(i)
                            val handleName = whisperMessage.getString(KEY_HANDLE)
                            val messageStr = whisperMessage.getString(ACTION_MESSAGE)
                            val isPerformer = whisperMessage.getString(KEY_USER_TYPE) == PERFORMER
                            updateWhisperLog(messageStr, handleName, isPerformer)
                            i++
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    ACTION_CHANGE_CHAT_STATUS -> {
                        _viewerStatus.postValue(jsonMessage.getString(BUNDLE_KEY.STATUS).toInt())
                    }
                }
                if (jsonMessage.has(KEY_TWO_SHOT) && jsonMessage.getString(KEY_TWO_SHOT) != _twoShot.value) {
                    val newTwoShotValue: String = jsonMessage.getString(KEY_TWO_SHOT)
                    if (newTwoShotValue == TWO_SHOT_VALUE_2) {
                        _updateUIMode.postValue(UI_DISMISS_PRIVATE_MODE)
                    } else if (newTwoShotValue == TWO_SHOT_VALUE_0 && _twoShot.value == TWO_SHOT_VALUE_2) {
                        _connectResult.postValue(
                            ConnectResult(
                                RESULT_NG,
                                application.getString(R.string.dialog_destroy_private_mode)
                            )
                        )
                        maruCastManager.stopPublish()
                    }
                    _twoShot.postValue(newTwoShotValue)
                }

                if (!jsonMessage.has(KEY_TWO_SHOT_CANCEL) && jsonMessage.has(KEY_TWO_SHOT_CANCEL_2)
                    && jsonMessage.getString(KEY_TWO_SHOT_CANCEL_2) == PERFORMER
                ) {
                    _updateUIMode.postValue(UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE) // TODO Dismiss & show confirm dialog
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TWO_SHOT_VALUE_0 = "0"
        const val TWO_SHOT_VALUE_2 = "2"

        const val UI_NORMAL_MODE = 0
        const val UI_DISMISS_PRIVATE_MODE = 1
        const val UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE = 2
        const val UI_SHOW_WAITING_PRIVATE_MODE = 3
        const val UI_BUY_POINT = 4

        // MODE
        const val PARTY = 0
        const val PRIVATE = 10
        const val PRIVATE_CANCEL = 11
        const val PREMIUM_PRIVATE = 30
        const val BUY_POINT = 20
    }
}

sealed class LiveStreamActionState {
    object ChangeToPremiumPrivateFromPrivate : LiveStreamActionState()
    object OpenBottomSheetSettingCameraAndMic : LiveStreamActionState()
    object EndCall : LiveStreamActionState()
    object ShowDialogWarningPoint : LiveStreamActionState()
}

sealed class PointState {
    object StartCheck : PointState()
    object PointUnder1000 : PointState()
    object PointUnder500 : PointState()
    object EndCheck : PointState()
}