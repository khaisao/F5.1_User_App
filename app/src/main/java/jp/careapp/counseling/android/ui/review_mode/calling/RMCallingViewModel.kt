package jp.careapp.counseling.android.ui.review_mode.calling

import android.app.Application
import androidx.hilt.Assisted
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SafeCallApi
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.BuildConfig
import jp.careapp.counseling.android.data.model.live_stream.ConnectResult
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.data.network.FssMemberAuthResponse
import jp.careapp.counseling.android.data.network.socket.SocketSendMessage
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.network.socket.CallingWebSocketClient
import jp.careapp.counseling.android.network.socket.FlaxWebSocketManager
import jp.careapp.counseling.android.network.socket.MaruCastManager
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SocketInfo
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

const val TAG = "CallingViewModel"

@HiltViewModel
class RMCallingViewModel @Inject constructor(
    @Assisted
    savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,

    ) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack,
    CallingWebSocketClient.MaruCastLoginCallBack {
    val actionState = SingleLiveEvent<jp.careapp.counseling.android.utils.ActionState>()

    var userCode: String = ""

    val isButtonEnable = MutableLiveData<Boolean>()
    val isLoginSuccess = MutableLiveData(false)
    val connectResult = MutableLiveData<ConnectResult>()
    private var cancelButtonClickedFlag = false
    var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null
    var viewerStatus: Int = 0

    private var lastPoint = 0

    init {
        userCode = savedStateHandle.get<String>(BUNDLE_KEY.PERFORMER_CODE).toString()
        getConfigCall()
    }

    private fun getConfigCall() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                apiInterface.getConfigCall().let {
                    Timber.d("getConfigCall: ${SafeCallApi.gson.toJson(it)}")
                    rxPreferences.saveConfigCall(it)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun resetData() {
        connectResult.value = ConnectResult(result = SocketInfo.RESULT_NONE)
        isLoginSuccess.value = false
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
                        append("?action=${SocketInfo.ACTION_CALL}")
                        append("&roomCode=$performerCode")
                        append("&token=$token")
                        append("&performerCode=$performerCode")
                        append("&ownerCode=${Define.OWNER_CODE}")
                    }
                    flaxWebSocketManager.flaxConnect(urlStartCall, this@RMCallingViewModel)
                    lastPoint = rxPreferences.getPoint()
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun connectLiveStream(performerCode: String, status: PerformerStatus) {
        if (viewerStatus == 0 && status == PerformerStatus.WAITING) {
            startCall(performerCode)
        } else {
            connectFlaxChatSocket(performerCode, viewerStatus)
        }
    }

    fun cancelCall() {
        flaxWebSocketManager.sendMessage(SafeCallApi.gson.toJson(SocketSendMessage(action = SocketInfo.ACTION_CANCEL_CALL)))
    }

    private fun handlePerformerLogin() {
        flaxWebSocketManager.flaxLogout()
        connectFlaxChatSocket(userCode, viewerStatus)
    }

    override fun onHandleMessage(jsonMessage: JSONObject) {
        try {
            val action = if (jsonMessage.has(SocketInfo.KEY_ACTION)) jsonMessage.getString(
                SocketInfo.KEY_ACTION
            ) else ""
            val result = if (jsonMessage.has(SocketInfo.KEY_RESULT)) jsonMessage.getString(
                SocketInfo.KEY_RESULT
            ) else ""
            val isNeedCall: Boolean? =
                if (jsonMessage.has(SocketInfo.KEY_IS_NEED_CALL)) jsonMessage.getBoolean(SocketInfo.KEY_IS_NEED_CALL) else null
            if (result == SocketInfo.RESULT_NG || action == SocketInfo.ACTION_PERFORMER_RESPONSE) {
                handleNGResponse(jsonMessage)
            } else if (action == SocketInfo.ACTION_LOGIN_REQUEST && isNeedCall != null && isNeedCall) {
                handleLoginRequest()
            } else if (action == SocketInfo.ACTION_PERFORMER_LOGIN || isNeedCall != null && !isNeedCall) {
                // isNeedCallがfalseの場合はパフォーマー側が配信しているのでそのままチャット画面に遷移する
                handlePerformerLogin()
            } else if (action == SocketInfo.ACTION_LOGIN) {
                handleLogin(jsonMessage)
            } else if (action == SocketInfo.ACTION_CANCEL_CALL) {
                cancelButtonClickedFlag = true
                flaxWebSocketManager.flaxLogout()
                isButtonEnable.postValue(true)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class)
    private fun handleNGResponse(message: JSONObject) {
        if (!cancelButtonClickedFlag) {
            // エラーメッセージがerrorに入っていたりmessageに入っていたりするので以下のようにする必要がある
            val errorMessage =
                when {
                    message.has(SocketInfo.ACTION_MESSAGE) -> message.getString(SocketInfo.ACTION_MESSAGE)
                    message.has(SocketInfo.KEY_ERROR) -> message.getString(SocketInfo.KEY_ERROR)
                    else -> "拒否されました"
                }
            connectResult.postValue(ConnectResult(SocketInfo.RESULT_NG, errorMessage))
            flaxWebSocketManager.flaxLogout()
        }
        if (message.has(SocketInfo.KEY_ERROR) && message.getString(SocketInfo.KEY_ERROR) == "トークンが無効です") {
            viewModelScope.launch {
                refreshCallToken()
            }
        }
        isButtonEnable.postValue(true)
        cancelButtonClickedFlag = false
    }

    private fun handleLoginRequest() {
        connectResult.postValue(ConnectResult(SocketInfo.RESULT_OK))
    }

    @Throws(JSONException::class)
    private fun handleLogin(message: JSONObject) {
        flaxLoginAuthResponse = FlaxLoginAuthResponse(
            message.getString(SocketInfo.KEY_MEMBER_CODE),
            message.getString(SocketInfo.KEY_PERFORMER_CODE),
            message.getString(SocketInfo.KEY_MEDIA_SERVER_OWNER_CODE),
            message.getString(SocketInfo.KEY_MEDIA_SERVER),
            message.getString(SocketInfo.KEY_SESSION_CODE),
            message.getString(SocketInfo.KEY_PERFORMER_THUMB_IMAGE),
            message.getInt(BUNDLE_KEY.STATUS)
        )
        maruCastManager.setLoginCallBack(this)
        maruCastManager.connectServer(flaxLoginAuthResponse!!)
    }

    override fun loginSuccess() {
        isButtonEnable.postValue(true)
        isLoginSuccess.postValue(true)
        rxPreferences.setCallToken("")
    }

    private fun connectFlaxChatSocket(performerCode: String, callType: Int) {
        val param = JSONObject()
        try {
            param.put(SocketInfo.AUTH_OWN_NAME, BuildConfig.WS_OWNER)
            param.put(SocketInfo.KEY_PERFORMER_CODE, performerCode)
            param.put(SocketInfo.AUTH_TOKEN, rxPreferences.getCallToken())
            param.put(BUNDLE_KEY.STATUS, callType)
            val urlStr: String =
                BuildConfig.WS_URL_LOGIN_CALL + "?data=" + URLEncoder.encode(
                    param.toString(),
                    "UTF-8"
                )
            flaxWebSocketManager.flaxConnect(urlStr, this)
            rxPreferences.setCallToken("")
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}

data class PerformerInfo(
    var name: String = "",
    var performerCode: String = "",
    var imageUrl: String = "",
) : Serializable
