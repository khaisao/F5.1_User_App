package jp.careapp.counseling.android.ui.review_mode.live_stream

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
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.SocketInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.marge.marucast_android_client.views.VideoRendererView
import javax.inject.Inject

@HiltViewModel
class RMLiveStreamViewModel @Inject constructor(
    private val application: Application,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,
    private val audioManager: AudioManager,
    private val rxPreferences: RxPreferences,
) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack {

    private val _messageList = MutableLiveData<ArrayList<LiveStreamChatResponse>>()
    val messageList: LiveData<ArrayList<LiveStreamChatResponse>>
        get() = _messageList

    private val _connectResult = MutableLiveData<ConnectResult>()
    val connectResult: LiveData<ConnectResult>
        get() = _connectResult

    private val _updateUIMode = MutableLiveData(LiveStreamViewModel.UI_NORMAL_MODE)
    val updateUIMode: LiveData<Int>
        get() = _updateUIMode
    private val _twoShot = MutableLiveData<String>()
    val twoShot: LiveData<String>
        get() = _twoShot
    private val _viewerStatus = MutableLiveData<Int>()
    val viewerStatus: LiveData<Int>
        get() = _viewerStatus

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

    fun setViewerStatus(status: Int) {
        _viewerStatus.value = status
    }

    fun setFlaxLoginAuthResponse(response: FlaxLoginAuthResponse?) {
        flaxLoginAuthResponse = response
    }

    fun updateMode(mode: Int) {
        when (mode) {
            LiveStreamViewModel.PARTY -> {
                if (_viewerStatus.value == 1) {
                    flaxWebSocketManager.changeStatus()
                } else {
                    flaxWebSocketManager.privateModeDestroy()
                }
            }
            LiveStreamViewModel.PREMIUM_PRIVATE -> {
                updateCameraSetting(false)
                updateMicSetting(false)
                flaxWebSocketManager.privateModeInvitation()
                _updateUIMode.postValue(LiveStreamViewModel.UI_SHOW_WAITING_PRIVATE_MODE)
            }
        }
    }

    fun logout() {
        audioManager.mode = oldAudioMode
        audioManager.isSpeakerphoneOn = oldSpeakerPhone
        maruCastManager.setLiveStreamStateOn(false)
        maruCastManager.logoutRoom()
        flaxWebSocketManager.flaxLogout()
    }

    fun privateModeCancel() {
        flaxWebSocketManager.privateModeCancel()
    }

    fun handleConnect(rendererView: VideoRendererView?, callBack: MaruCastManager.SwitchViewerCallback?) {
        maruCastManager.removeCallBack()
        maruCastManager.setSwitchViewerCallBack(callBack)
        maruCastManager.setLiveStreamStateOn(true)
        maruCastManager.playStream(rendererView)
    }

    fun setAudioConfig(audioMode: Int, isSpeakerPhoneOn: Boolean) {
        audioManager.mode = audioMode
        audioManager.isSpeakerphoneOn = isSpeakerPhoneOn
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

    override fun onHandleMessage(jsonMessage: JSONObject) {
        try {
            val result: String = jsonMessage.getString(SocketInfo.KEY_RESULT)
            val action: String? =
                if (jsonMessage.has(SocketInfo.KEY_ACTION)) jsonMessage.getString(SocketInfo.KEY_ACTION) else null
            if (action == null) {
                if (isLogout) return
                maruCastManager.setRoomExists(
                    !(jsonMessage.has(SocketInfo.KEY_ERROR) && jsonMessage.getString(
                        SocketInfo.KEY_ERROR
                    ).contains("オフライン"))
                )
                _connectResult.postValue(
                    ConnectResult(
                        SocketInfo.RESULT_NG,
                        jsonMessage.getString(SocketInfo.KEY_ERROR),
                        isLogout = false
                    )
                )
                isLogout = false
            } else if (result == SocketInfo.RESULT_NG) {
                val errorMsg =
                    if (jsonMessage.has(SocketInfo.KEY_ERROR)) jsonMessage.getString(SocketInfo.KEY_ERROR) else "エラーが発生しました"
                when (action) {
                    SocketInfo.ACTION_ASK_TWO_SHOT, SocketInfo.ACTION_CANCEL_TWO_SHOT -> {
                        _connectResult.postValue(ConnectResult(SocketInfo.RESULT_NG, errorMsg))
                        _updateUIMode.postValue(LiveStreamViewModel.UI_DISMISS_PRIVATE_MODE)
                    }
                    else -> _connectResult.postValue(ConnectResult(SocketInfo.RESULT_NG, errorMsg, true))
                }
            } else if (result == SocketInfo.RESULT_OK) {
                when (action) {
                    SocketInfo.ACTION_RELOAD, SocketInfo.ACTION_CHAT_LOG -> try {
                        val chatMessageArray: JSONArray = jsonMessage.getJSONArray(SocketInfo.KEY_CHAT)
                        run {
                            var i = 0
                            while (i < chatMessageArray.length()) {
                                val chatMessage = chatMessageArray.getJSONObject(i)
                                val handleName = chatMessage.getString(SocketInfo.KEY_HANDLE)
                                val messageStr = chatMessage.getString(SocketInfo.ACTION_MESSAGE)
                                val userCode = chatMessage.getString(SocketInfo.KEY_USER_CODE)
                                if (userCode != flaxLoginAuthResponse?.memberCode) {
                                    val isPerformer =
                                        chatMessage.getString(SocketInfo.KEY_USER_TYPE) == BUNDLE_KEY.PERFORMER
                                    updateChatLog(messageStr, handleName, isPerformer)
                                }
                                i++
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    SocketInfo.ACTION_CHANGE_CHAT_STATUS -> {
                        _viewerStatus.postValue(jsonMessage.getString(BUNDLE_KEY.STATUS).toInt())
                    }
                }
                if (jsonMessage.has(SocketInfo.KEY_TWO_SHOT) && jsonMessage.getString(SocketInfo.KEY_TWO_SHOT) != _twoShot.value) {
                    val newTwoShotValue: String = jsonMessage.getString(SocketInfo.KEY_TWO_SHOT)
                    if (newTwoShotValue == LiveStreamViewModel.TWO_SHOT_VALUE_2) {
                        _updateUIMode.postValue(LiveStreamViewModel.UI_DISMISS_PRIVATE_MODE)
                    } else if (newTwoShotValue == LiveStreamViewModel.TWO_SHOT_VALUE_0 && _twoShot.value == LiveStreamViewModel.TWO_SHOT_VALUE_2) {
                        _connectResult.postValue(
                            ConnectResult(
                                SocketInfo.RESULT_NG,
                                application.getString(R.string.dialog_destroy_private_mode)
                            )
                        )
                        maruCastManager.stopPublish()
                    }
                    _twoShot.postValue(newTwoShotValue)
                }

                if (!jsonMessage.has(SocketInfo.KEY_TWO_SHOT_CANCEL) && jsonMessage.has(SocketInfo.KEY_TWO_SHOT_CANCEL_2)
                    && jsonMessage.getString(SocketInfo.KEY_TWO_SHOT_CANCEL_2) == BUNDLE_KEY.PERFORMER
                ) {
                    _updateUIMode.postValue(LiveStreamViewModel.UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE) // TODO Dismiss & show confirm dialog
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun handleSocketCallback() {
        flaxWebSocketManager.setCallback(this)
    }

    fun sendComment(message: String) {
        if (message.isEmpty()) return
        flaxWebSocketManager.sendMessage(message)
        updateChatLog(message, name = rxPreferences.getNickName() ?: "あなた", false)
    }
}
