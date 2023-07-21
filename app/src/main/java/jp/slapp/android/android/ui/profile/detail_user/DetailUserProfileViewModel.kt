package jp.slapp.android.android.ui.profile.detail_user

import android.app.Activity
import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.base.NetworkException
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.data.model.live_stream.ConnectResult
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.FlaxLoginAuthResponse
import jp.slapp.android.android.data.network.FssMemberAuthResponse
import jp.slapp.android.android.data.network.GalleryResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.network.socket.CallingWebSocketClient
import jp.slapp.android.android.network.socket.FlaxWebSocketManager
import jp.slapp.android.android.network.socket.MaruCastManager
import jp.slapp.android.android.utils.BUNDLE_KEY.Companion.STATUS
import jp.slapp.android.android.utils.Define
import jp.slapp.android.android.utils.SocketInfo
import jp.slapp.android.android.utils.SocketInfo.ACTION_CANCEL_CALL
import jp.slapp.android.android.utils.SocketInfo.ACTION_LOGIN
import jp.slapp.android.android.utils.SocketInfo.ACTION_LOGIN_REQUEST
import jp.slapp.android.android.utils.SocketInfo.ACTION_MESSAGE
import jp.slapp.android.android.utils.SocketInfo.ACTION_PERFORMER_LOGIN
import jp.slapp.android.android.utils.SocketInfo.ACTION_PERFORMER_RESPONSE
import jp.slapp.android.android.utils.SocketInfo.AUTH_OWN_NAME
import jp.slapp.android.android.utils.SocketInfo.AUTH_TOKEN
import jp.slapp.android.android.utils.SocketInfo.KEY_ACTION
import jp.slapp.android.android.utils.SocketInfo.KEY_ERROR
import jp.slapp.android.android.utils.SocketInfo.KEY_IS_NEED_CALL
import jp.slapp.android.android.utils.SocketInfo.KEY_MEDIA_SERVER
import jp.slapp.android.android.utils.SocketInfo.KEY_MEDIA_SERVER_OWNER_CODE
import jp.slapp.android.android.utils.SocketInfo.KEY_MEMBER_CODE
import jp.slapp.android.android.utils.SocketInfo.KEY_PERFORMER_CODE
import jp.slapp.android.android.utils.SocketInfo.KEY_PERFORMER_THUMB_IMAGE
import jp.slapp.android.android.utils.SocketInfo.KEY_RESULT
import jp.slapp.android.android.utils.SocketInfo.KEY_SESSION_CODE
import jp.slapp.android.android.utils.SocketInfo.RESULT_NG
import jp.slapp.android.android.utils.SocketInfo.RESULT_NONE
import jp.slapp.android.android.utils.SocketInfo.RESULT_OK
import jp.slapp.android.android.utils.formatDecimalSeparator
import jp.slapp.android.android.utils.performer_extension.PerformerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class DetailUserProfileViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,
    private val application: Application
) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack,
    CallingWebSocketClient.MaruCastCallBack {
    val userProfileResult = MutableLiveData<ConsultantResponse?>()
    val userGallery = MutableLiveData<List<GalleryResponse>?>()
    val statusFavorite = MutableLiveData<Boolean>()
    val statusRemoveFavorite = MutableLiveData<Boolean>()
    val isFirstChat = MutableLiveData<Boolean>()
    val isButtonEnable = MutableLiveData<Boolean>()
    val isLoginSuccess = MutableLiveData(false)
    val connectResult = MutableLiveData<ConnectResult>()
    val currentPoint = MutableStateFlow("")
    private var cancelButtonClickedFlag = false
    var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null
    var viewerStatus: Int = 0

    val blockUserResult = MutableLiveData<Boolean>()

    private val gson by lazy { Gson() }

    init {
        getConfigCall()
    }

    fun getMemberInfo() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = apiInterface.getMember()
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        rxPreferences.saveMemberInfoEditProfile(
                            dataResponse.name,
                            dataResponse.mail,
                            dataResponse.age,
                            dataResponse.birth,
                            dataResponse.sex,
                            dataResponse.point,
                            dataResponse.pushMail,
                            dataResponse.receiveNoticeMail,
                            dataResponse.receiveNewsletterMail
                        )
                        withContext(Dispatchers.Main) {
                            currentPoint.value = rxPreferences.getPoint().formatDecimalSeparator()
                        }
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

    private fun getConfigCall() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                apiInterface.getConfigCall().let {
                    Timber.d("getConfigCall: ${gson.toJson(it)}")
                    rxPreferences.saveConfigCall(it)
                    refreshCallToken().apply {
                        rxPreferences.setCallToken(token ?: "")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private suspend fun refreshCallToken(): FssMemberAuthResponse {
        val urlAuth = buildString {
            append(rxPreferences.getConfigCall().fssMemberAppAuthUrl)
            append("?id=${URLEncoder.encode(rxPreferences.getEmail(), "UTF-8")}")
            append("&pass=${rxPreferences.getPassword() ?: ""}")
        }
        return apiInterface.fssMemberAppAuth(urlAuth)
    }

    private fun startCall(performerCode: String) {
        val urlStartCall = buildString {
            append(rxPreferences.getConfigCall().wsMemberLoginRequest)
            append("?action=${SocketInfo.ACTION_CALL}")
            append("&roomCode=$performerCode")
            append("&token=${rxPreferences.getCallToken()}")
            append("&performerCode=$performerCode")
            append("&ownerCode=${BuildConfig.OWNER_CODE}")
        }
        flaxWebSocketManager.flaxConnect(urlStartCall, this@DetailUserProfileViewModel)
    }

    fun connectLiveStream(performerCode: String, status: PerformerStatus) {
        if (viewerStatus == 0 && status == PerformerStatus.WAITING) {
            startCall(performerCode)
        } else {
            connectFlaxChatSocket(performerCode, viewerStatus)
        }
    }

    fun cancelCall(isError: Boolean) {
        connectResult.value = ConnectResult(result = RESULT_NONE)
        if (!isError) {
            flaxWebSocketManager.cancelCall()
        }
    }

    fun loadDetailUser(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getUserProfileDetail(code)
                val galleryResponse = apiInterface.getUserGallery(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        userProfileResult.postValue(it.dataResponse)
                    } else {
                        userProfileResult.postValue(null)
                    }
                }
                galleryResponse.let {
                    if (it.errors.isEmpty()) {
                        userGallery.postValue(it.dataResponse)
                    } else {
                        userGallery.postValue(null)
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                userProfileResult.postValue(null)
                userGallery.postValue(null)
                isLoading.value = false
            }
        }
    }

    fun handleClickBlock(performerCode: String, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.handleClickblock(performerCode)
                response.let {
                    if (it.errors.isEmpty()) {
                        blockUserResult.value = true
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        handleClickBlock(
                            performerCode,
                            activity
                        )
                    }
                )
            }
        }
    }

    /**
     * add user to list profile
     */
    fun addUserToFavorite(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.addUserToFavorite(code)
                response.let {
                    statusFavorite.value = it.errors.isEmpty()
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                statusFavorite.value = false
                isLoading.value = false
                if (throwable !is NetworkException) {
                    loadDetailUser(code)
                }
            }
        }
    }

    /**
     * remove usser in list profile
     */
    fun removeUserToFavorite(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.removeUserToFavorite(code)
                response.let {
                    statusRemoveFavorite.value = it.errors.isEmpty()
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                statusRemoveFavorite.value = false
                isLoading.value = false
                if (throwable !is NetworkException) {
                    loadDetailUser(code)
                }
            }
        }
    }


    fun resetData() {
        connectResult.value = ConnectResult(result = RESULT_NONE)
        isLoginSuccess.value = false
    }

    override fun onHandleMessage(jsonMessage: JSONObject) {
        try {
            val action = if (jsonMessage.has(KEY_ACTION)) jsonMessage.getString(KEY_ACTION) else ""
            val result = if (jsonMessage.has(KEY_RESULT)) jsonMessage.getString(KEY_RESULT) else ""
            val isNeedCall: Boolean? =
                if (jsonMessage.has(KEY_IS_NEED_CALL)) jsonMessage.getBoolean(KEY_IS_NEED_CALL) else null
            if (result == RESULT_NG || action == ACTION_PERFORMER_RESPONSE) {
                handleNGResponse(jsonMessage)
            } else if (action == ACTION_LOGIN_REQUEST && isNeedCall != null && isNeedCall) {
                handleLoginRequest()
            } else if (action == ACTION_PERFORMER_LOGIN || isNeedCall != null && !isNeedCall) {
                // isNeedCallがfalseの場合はパフォーマー側が配信しているのでそのままチャット画面に遷移する
                handlePerformerLogin()
            } else if (action == ACTION_LOGIN) {
                handleLogin(jsonMessage)
            } else if (action == ACTION_CANCEL_CALL) {
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
                    message.has(ACTION_MESSAGE) -> message.getString(ACTION_MESSAGE)
                    message.has(KEY_ERROR) -> message.getString(KEY_ERROR)
                    else -> "拒否されました"
                }
            connectResult.postValue(ConnectResult(RESULT_NG, errorMessage))
            flaxWebSocketManager.flaxLogout()
        }
        if (message.has(KEY_ERROR) && message.getString(KEY_ERROR) == "トークンが無効です") {
            viewModelScope.launch {
                refreshCallToken()
            }
        }
        isButtonEnable.postValue(true)
        cancelButtonClickedFlag = false
    }

    private fun handleLoginRequest() {
        connectResult.postValue(ConnectResult(RESULT_OK))
    }

    private fun handlePerformerLogin() {
        flaxWebSocketManager.flaxLogout()
        userProfileResult.value?.code?.let { performerCode ->
            connectFlaxChatSocket(performerCode, viewerStatus)
        }
    }

    @Throws(JSONException::class)
    private fun handleLogin(message: JSONObject) {
        flaxLoginAuthResponse = FlaxLoginAuthResponse(
            message.getString(KEY_MEMBER_CODE),
            message.getString(KEY_PERFORMER_CODE),
            message.getString(KEY_MEDIA_SERVER_OWNER_CODE),
            message.getString(KEY_MEDIA_SERVER),
            message.getString(KEY_SESSION_CODE),
            message.getString(KEY_PERFORMER_THUMB_IMAGE),
            message.getInt(STATUS)
        )
        maruCastManager.setCallBack(this)
        maruCastManager.loginRoom(flaxLoginAuthResponse!!, application.applicationContext)
    }

    override fun remoteTrackCompleted() {
        isButtonEnable.postValue(true)
        isLoginSuccess.postValue(true)
        rxPreferences.setCallToken("")
    }

    private fun connectFlaxChatSocket(performerCode: String, callType: Int) {
        val param = JSONObject()
        try {
            param.put(AUTH_OWN_NAME, BuildConfig.WS_OWNER)
            param.put(KEY_PERFORMER_CODE, performerCode)
            param.put(AUTH_TOKEN, rxPreferences.getCallToken())
            param.put(STATUS, callType)
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
