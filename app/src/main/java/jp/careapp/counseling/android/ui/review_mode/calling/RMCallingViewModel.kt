package jp.careapp.counseling.android.ui.review_mode.calling

import android.app.Application
import android.os.SystemClock
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveData
import jp.careapp.counseling.BuildConfig
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.data.network.FssMemberAuthResponse
import jp.careapp.counseling.android.data.network.socket.*
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.network.socket.CallingWebSocketClient
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CALL
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CANCEL_CALL
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CHAT_LOG
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_LOGIN
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_LOGIN_REQUEST
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_PERFORMER_LOGIN
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_PERFORMER_RESPONSE
import jp.careapp.counseling.android.utils.calling.CallSoundManager
import jp.careapp.counseling.android.utils.calling.MediaServerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URLEncoder
import java.util.*

const val TAG = "CallingViewModel"

class RMCallingViewModel @ViewModelInject constructor(
    private val application: Application,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val callSoundManager: CallSoundManager
) : BaseViewModel() {

    private val _isConfigAudioCall: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isConfigAudioCall: MutableLiveData<Boolean?> = _isConfigAudioCall
    private val _minimizeCallState = MutableLiveData<MinimizeCallState>(MinimizeCallState.INACTIVE)
    val minimizeCallState: MutableLiveData<MinimizeCallState> = _minimizeCallState
    private val _performerInfo = MutableLiveData<PerformerInfo>(null)
    val performerInfo: MutableLiveData<PerformerInfo> = _performerInfo
    private val _callState = MutableLiveData<CallState?>(null)
    val callState: MutableLiveData<CallState?> = _callState
    private val _isMuteMic = MutableLiveData(false)
    val isMuteMic: MutableLiveData<Boolean> = _isMuteMic
    private val _isMuteSpeaker = MutableLiveData(true)
    val isMuteSpeaker: MutableLiveData<Boolean> = _isMuteSpeaker
    private val _callDuration = MutableLiveData(0)
    val callDuration: MutableLiveData<Int> = _callDuration
    private val _actionState = SingleLiveData<ActionState>()
    val actionState: SingleLiveData<ActionState> = _actionState

    private lateinit var socketClient: CallingWebSocketClient
    private lateinit var mediaServer: MediaServerManager
    private val gson by lazy { Gson() }
    private var performer = PerformerInfo()
    private var previousAudioConfig: AudioConfig? = null
    private var timer: Timer? = null
    private var lastPoint = 0

    init {
        getConfigCall()
    }

    private fun getConfigCall() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                apiInterface.getConfigCall().let {
                    Timber.tag(TAG).d("getConfigCall: ${gson.toJson(it)}")
                    rxPreferences.saveConfigCall(it)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e)
            }
        }
    }

    private suspend fun refreshCallToken(): FssMemberAuthResponse {
        val urlAuth = buildString {
            append(rxPreferences.getConfigCall().fssMemberAppAuthUrl)
            append("?id=${URLEncoder.encode(rxPreferences.getEmail(), "UTF-8")}")
            append("&pass=${rxPreferences.getPassword()}")
        }
        return apiInterface.fssMemberAppAuth(urlAuth)
    }

    private fun startCall(performerCode: String) {
        viewModelScope.launch {
            try {
                refreshCallToken().apply {
                    rxPreferences.setCallToken(token ?: "")
                    val urlStartCall = buildString {
                        append(rxPreferences.getConfigCall().wsMemberLoginRequest)
                        append("?action=$ACTION_CALL")
                        append("&roomCode=$performerCode")
                        append("&token=$token")
                        append("&performerCode=$performerCode")
                        append("&ownerCode=${Define.OWNER_CODE}")
                    }
                    socketClient = CallingWebSocketClient {
                        handleSocketMessage(it)
                    }
                    socketClient.connect(urlStartCall)
                    lastPoint = rxPreferences.getPoint()
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e)
            }
        }
    }

    private fun endCall() {
        if (::mediaServer.isInitialized) mediaServer.logoutRoom()
        if (::socketClient.isInitialized) socketClient.closeWebSocket()
        _actionState.postValue(ActionState.EndCall)
        callSoundManager.stopRingBack()
        stopTimer()
        resetData()
    }

    fun onEndCall() {
        when (_callState.value) {
            CallState.CONNECTING -> cancelCall()
            CallState.TALKING -> endCall()
            else -> {}
        }
    }

    private fun cancelCall() {
        socketClient.sendMessage(gson.toJson(SocketSendMessage(action = ACTION_CANCEL_CALL)))
    }

    private fun startTimer() {
        val initialTime = SystemClock.elapsedRealtime()
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val newValue: Long = (SystemClock.elapsedRealtime() - initialTime) / 1000
                    _callDuration.postValue(newValue.toInt())
                }
            }, 1000L, 1000L)
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    private fun handleSocketMessage(message: String) {
        gson.fromJson(message, BaseSocketReceiveMessage::class.java)?.let { data ->
            if (data.isResultOK()) {
                when (data.action) {
                    ACTION_LOGIN_REQUEST -> {
                        _callState.postValue(CallState.CONNECTING)
                        callSoundManager.playRingBack()
                    }
                    ACTION_PERFORMER_LOGIN -> {
                        handlePerformerLogin()
                    }
                    ACTION_LOGIN -> {
                        gson.fromJson(message, LoginMessage::class.java)?.let {
                            handleLogin(it)
                        }
                    }
                    ACTION_CHAT_LOG -> {
                        gson.fromJson(message, ChatLogMessage::class.java)?.let {
                            handleChatLog(it)
                        }
                    }
                    ACTION_CANCEL_CALL, ACTION_PERFORMER_RESPONSE -> {
                        endCall()
                    }
                    else -> {}
                }
            } else {
                Timber.tag(TAG).e(data.error)
                viewModelScope.launch {
                    stopTimer()
                    callSoundManager.stopRingBack()
                    endCall()
                }
            }
        }
    }

    private fun handlePerformerLogin() {
        socketClient.closeWebSocket()
        LoginMemberParam(
            BuildConfig.WS_OWNER,
            performer.performerCode,
            rxPreferences.getCallToken(),
            0
        ).let {
            val url = BuildConfig.WS_URL_LOGIN_CALL + "?data=" + URLEncoder.encode(
                gson.toJson(it),
                "UTF-8"
            )
            socketClient.connect(url)
        }
    }

    private fun handleLogin(data: LoginMessage) {
        FlaxLoginAuthResponse(
            data.memberCode,
            data.performerCode,
            data.mediaServerOwnerCode,
            data.mediaServer,
            data.sessionCode,
            data.performerThumbnailImage
        ).let { dataLogin ->
            mediaServer = MediaServerManager(application.applicationContext)
            mediaServer.apply {
                loginToRoom(dataLogin) {
                    callSoundManager.stopRingBack()
                    mediaServer.publishStream()
                    _callState.postValue(CallState.TALKING)
                    _isConfigAudioCall.postValue(true)
                    startTimer()
                }
                setOnPublishSuccessListener {
                    val isMuteAudio = _isMuteMic.value ?: false
                    mediaServer.muteAudio(isMuteAudio)
                }
            }
        }
    }

    private fun handleChatLog(chatLog: ChatLogMessage) {
        val currentPoint = chatLog.point.toInt()
        if (currentPoint < 1000 && lastPoint >= 1000) {
            _actionState.postValue(ActionState.ShowDialogWarningPoint)
        }
        lastPoint = currentPoint
    }

    fun showMinimizeCall(isShow: Boolean) {
        if (isShow) {
            _minimizeCallState.postValue(MinimizeCallState.ACTIVE(performer.imageUrl))
        } else {
            _minimizeCallState.postValue(MinimizeCallState.INACTIVE)
        }
    }

    fun changeMic() {
        _isMuteMic.value?.let {
            if (::mediaServer.isInitialized) mediaServer.muteAudio(!it)
            _isMuteMic.value = !it
        }
    }

    fun changeSpeaker() {
        _isMuteSpeaker.value?.let {
            _isMuteSpeaker.value = !it
        }
    }

    fun setPerformerInfo(name: String, performerCode: String, imageUrl: String) {
        performer.let {
            it.name = name
            it.performerCode = performerCode
            it.imageUrl = imageUrl
        }
        _performerInfo.value = performer
        startCall(performer.performerCode)
    }

    fun getPerformerInfo(): PerformerInfo {
        return performer
    }

    fun savePreviousAudioConfig(mode: Int, isSpeakerphoneOn: Boolean) {
        previousAudioConfig = AudioConfig(mode, isSpeakerphoneOn)
    }

    fun getPreviousAudioConfig(): AudioConfig? {
        return previousAudioConfig
    }

    private fun resetData() {
        performer = PerformerInfo()
        _isMuteMic.postValue(false)
        _isMuteSpeaker.postValue(true)
        _isConfigAudioCall.postValue(false)
        _minimizeCallState.postValue(MinimizeCallState.INACTIVE)
        _callState.postValue(null)
        _callDuration.postValue(0)
        lastPoint = 0
    }

    fun isFullMode(): Boolean {
        return rxPreferences.isFullMode()
    }

    fun isCalling(): Boolean {
        return _callState.value != null
    }
}

data class AudioConfig(
    var mode: Int,
    var isSpeakerphoneOn: Boolean
)

data class PerformerInfo(
    var name: String = "",
    var performerCode: String = "",
    var imageUrl: String = "",
)

sealed class CallState {
    object CONNECTING : CallState()
    object TALKING : CallState()
}

sealed class MinimizeCallState {
    class ACTIVE(val avatarUrl: String) : MinimizeCallState()
    object INACTIVE : MinimizeCallState()
}

sealed class ActionState {
    object EndCall : ActionState()
    object ShowDialogWarningPoint : ActionState()
}