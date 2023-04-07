package jp.careapp.counseling.android.ui.live_stream

import android.app.Activity
import android.app.Application
import android.media.AudioManager
import android.text.Html
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.live_stream.ConnectResult
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.android.data.network.socket.ChatLogMessage
import jp.careapp.counseling.android.data.network.socket.SocketSendMessage
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.network.socket.CallingWebSocketClient
import jp.careapp.counseling.android.network.socket.FlaxWebSocketManager
import jp.careapp.counseling.android.network.socket.MaruCastManager
import jp.careapp.counseling.android.ui.calling.PerformerInfo
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.PERFORMER
import jp.careapp.counseling.android.utils.SocketInfo
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val mRepository: LiveStreamRepository,
    private val application: Application,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,
    private val keyService: KeyService,
    private val audioManager: AudioManager
) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack {

    private val _performerInfo = MutableLiveData<PerformerInfo>(null)
    val performerInfo: MutableLiveData<PerformerInfo> get() = _performerInfo

    private val _isMuteMic = MutableLiveData(false)
    val isMuteMic: MutableLiveData<Boolean> = _isMuteMic
    private val _isMuteSpeaker = MutableLiveData(true)
    val isMuteSpeaker: MutableLiveData<Boolean> = _isMuteSpeaker

    private lateinit var socketClient: CallingWebSocketClient

    private var currentMode = LiveStreamMode.PARTY
    private val _currentModeLiveData = MutableLiveData(currentMode)
    val currentModeLiveData: LiveData<Int>
        get() = _currentModeLiveData

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

    val mActionState = SingleLiveEvent<LiveStreamActionState>()

    private var isLogout = false
    private var oldAudioMode = 0
    private var oldSpeakerPhone = false

    private val gson by lazy { Gson() }
    private var performer = PerformerInfo()
    private var lastPoint = 0
    private var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null

    init {
        configAudio()
        handleSocketCallback()
    }

    fun reloadMode() {
        _currentModeLiveData.value = currentMode
    }

    fun sendChatMessage(message: String) {
        if (message.isEmpty()) return
        flaxWebSocketManager.sendMessage(message)
        updateChatLog(message, name = "あなた", false)
    }

    fun setFlaxLoginAuthResponse(response: FlaxLoginAuthResponse?) {
        flaxLoginAuthResponse = response
    }

    fun setViewerStatus(status: Int) {
        _viewerStatus.value = status
    }

    fun setPerformerInfo(name: String, performerCode: String, imageUrl: String) {
        performer.let {
            it.name = name
            it.performerCode = performerCode
            it.imageUrl = imageUrl
        }
        _performerInfo.value = performer
    }

    fun changeMic() {
        _isMuteMic.value?.let {
            _isMuteMic.value = !it
        }
    }

    fun changeSpeaker() {
        _isMuteSpeaker.value?.let {
            _isMuteSpeaker.value = !it
        }
    }

    fun setSocketCallback(callback: CallingWebSocketClient.ChatWebSocketCallBack?) {
        flaxWebSocketManager.setCallback(callback)
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
            LOGOUT -> {
                _updateUIMode.postValue(UI_LOGOUT)
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

    private fun handleChatLog(chatLog: ChatLogMessage) {
        val currentPoint = chatLog.point.toInt()
        if (currentPoint < 1000 && lastPoint >= 1000) {
            mActionState.postValue(LiveStreamActionState.ShowDialogWarningPoint)
        }
        lastPoint = currentPoint
    }

    private fun endCall() {
        if (::socketClient.isInitialized) socketClient.closeWebSocket()
        mActionState.postValue(LiveStreamActionState.EndCall)
        resetData()
    }

    private fun cancelCall() {
        socketClient.sendMessage(gson.toJson(SocketSendMessage(action = SocketInfo.ACTION_CANCEL_CALL)))
    }

    private fun resetData() {
        performer = PerformerInfo()
        _isMuteMic.postValue(false)
        _isMuteSpeaker.postValue(true)
        lastPoint = 0
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
                        val chatMessageArray: JSONArray = jsonMessage.getJSONArray(KEY_CHAT)
                        run {
                            var i = 0
                            while (i < chatMessageArray.length()) {
                                val chatMessage = chatMessageArray.getJSONObject(i)
                                val handleName =
                                    Html.escapeHtml(chatMessage.getString(KEY_HANDLE))
                                val messageStr =
                                    Html.escapeHtml(chatMessage.getString(ACTION_MESSAGE))
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
                            val handleName = Html.escapeHtml(whisperMessage.getString(KEY_HANDLE))
                            val messageStr =
                                Html.escapeHtml(whisperMessage.getString(ACTION_MESSAGE))
                            var isPerformer = false
                            if (whisperMessage.getString(KEY_USER_TYPE) == PERFORMER) {
                                isPerformer = true
                                _connectResult.postValue(
                                    ConnectResult(
                                        RESULT_NG,
                                        handleName + "様よりささやきが通知されました",
                                        true
                                    )
                                )
                            }
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
        const val UI_LOGOUT = 5

        // MODE
        const val PARTY = 0
        const val PRIVATE = 10
        const val PRIVATE_CANCEL = 11
        const val PREMIUM_PRIVATE = 30
        const val BUY_POINT = 20
        const val LOGOUT = -1
    }
}

sealed class LiveStreamActionState {
    object ChangeToPremiumPrivateFromPrivate : LiveStreamActionState()
    object OpenBottomSheetSettingCameraAndMic : LiveStreamActionState()
    object EndCall : LiveStreamActionState()
    object ShowDialogWarningPoint : LiveStreamActionState()
}