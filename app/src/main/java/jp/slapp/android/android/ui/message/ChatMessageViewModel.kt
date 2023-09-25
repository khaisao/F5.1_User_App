package jp.slapp.android.android.ui.message

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.AppApplication
import jp.slapp.android.android.data.event.EventBusAction
import jp.slapp.android.android.data.model.live_stream.ConnectResult
import jp.slapp.android.android.data.model.message.*
import jp.slapp.android.android.data.model.user_profile.ActionLoadProfile
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.FlaxLoginAuthResponse
import jp.slapp.android.android.data.network.FreeTemplateResponse
import jp.slapp.android.android.data.network.FssMemberAuthResponse
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.network.socket.CallingWebSocketClient
import jp.slapp.android.android.network.socket.FlaxWebSocketManager
import jp.slapp.android.android.network.socket.MaruCastManager
import jp.slapp.android.android.ui.chatList.ChatListViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.BUNDLE_KEY.Companion.PERFORMER_MSG_SHOW_REVIEW_APP
import jp.slapp.android.android.utils.SocketInfo
import jp.slapp.android.android.utils.performer_extension.PerformerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class ChatMessageViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val flaxWebSocketManager: FlaxWebSocketManager,
    private val maruCastManager: MaruCastManager,
    private val application: Application
) : BaseViewModel(), CallingWebSocketClient.ChatWebSocketCallBack,
    CallingWebSocketClient.MaruCastCallBack {
    var page = 1

    // result handle show/hide/disable LoadMore
    var hiddenLoadMoreHandle = MutableLiveData<Int>()

    // result list message
    var messageResult = MutableLiveData<DataMessage>()

    // user detail info chatting
    val userProfileResult = MutableLiveData<ConsultantResponse>()

    // result when send 1 message
    val sendMessageResult = MutableLiveData<SendMessageResponse?>()
    var subtractPoint = MutableLiveData<Boolean>()

    // listen result of socket
    val responseSocket = MutableLiveData<SocketActionSend>()

    // result load info of self when update point
    val memberInFoResult = MutableLiveData<MemberResponse?>()

    // result when first send message,if send first success,remove first message
    val isCloseFirstMessageInLocal = MutableLiveData<Boolean>()

    // hash map contain list message filter to date
    var dataMessageMap = LinkedHashMap<String, MutableList<BaseMessageResponse>>()

    var openPayMessageResult = MutableLiveData<SendMessageResponse>()

    // result when load transmission message
    val isHasTransmissionMessage = MutableLiveData<Boolean>()

    // result when load transmission message
    val isEnoughMessageForReview = MutableLiveData<Boolean>()

    var isLoadMessageAfterSend = false
    var isMessageFromSocket = false

    private var activity: Activity? = null
    private var performerCode = ""

    val isEnableButtonSend = MutableLiveData<Boolean>()

    // Live stream
    private val gson by lazy { Gson() }
    val isButtonEnable = MutableLiveData<Boolean>()
    val isLoginSuccess = MutableLiveData(false)
    val connectResult = MutableLiveData<ConnectResult>()
    private var cancelButtonClickedFlag = false
    var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null
    var viewerStatus: Int = 0

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

    fun loadMessage(
        activity: Activity,
        performerCode: String,
        isLoadmore: Boolean,
        isShowLoading: Boolean = true
    ) {
        this.isLoadMessageAfterSend = false
        this.activity = activity
        this.performerCode = performerCode
        if (!isLoadmore) {
            page = 1
            dataMessageMap.clear()
        }
        val params = HashMap<String, Any>()
        if (performerCode != "")
            params[BUNDLE_KEY.PERFORMER_CODE] = performerCode
        else
            params[BUNDLE_KEY.PERFORMER_CODE] = "0"
        params[BUNDLE_KEY.PAGE] = page
        params[BUNDLE_KEY.LIMIT] = BUNDLE_KEY.LIMIT_20
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        viewModelScope.launch {
            if (!isLoadmore && isShowLoading) {
                isLoading.value = true
            }
            try {
                val responseMessage = apiInterface.loadMessage(params)
                if (responseMessage.errors.isEmpty()) {
                    if (responseMessage.pagination.total > PERFORMER_MSG_SHOW_REVIEW_APP) {
                        isEnoughMessageForReview.value = true
                    }
                    if (!responseMessage.dataResponse.isNullOrEmpty()) {
                        hiddenLoadMoreHandle.value = ChatListViewModel.ENABLE_LOAD_MORE
                        page += 1
                        val data = responseMessage.dataResponse
                        data.forEach {
                            val time = DateUtil.convertStringToDateString(
                                it.sendDate,
                                DateUtil.DATE_FORMAT_1,
                                DateUtil.DATE_FORMAT_3
                            )
                            if (dataMessageMap.containsKey(time)) {
                                dataMessageMap.get(time)?.add(it)
                            } else {
                                val listMessage = mutableListOf<BaseMessageResponse>()
                                listMessage.add(it)
                                dataMessageMap.put(time, listMessage)
                            }
                        }
                        val messageResultConvert = mutableListOf<BaseMessageResponse>()
                        for ((key, value) in dataMessageMap) {
                            messageResultConvert.addAll(value)
                            messageResultConvert.add(
                                TimeMessageResponse(
                                    time = key
                                )
                            )
                        }
                        messageResult.value =
                            DataMessage(isLoadmore, messageResultConvert.reversed())
                    } else {
                        if (isLoadmore) {
                            hiddenLoadMoreHandle.value = ChatListViewModel.DISABLE_LOAD_MORE
                        } else {
                            messageResult.value =
                                DataMessage(isLoadmore, mutableListOf())
                        }
                    }
                }
            } catch (throwable: Throwable) {
                if (!isLoadmore && isShowLoading) {
                    isLoading.value = false
                }
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadMessage(activity, performerCode, isLoadmore, isShowLoading)
                    }
                )
            } finally {
                if (isLoadmore) {
                    hiddenLoadMoreHandle.value = ChatListViewModel.HIDDEN_LOAD_MORE
                } else {
                    isLoading.value = false
                }
                AppApplication.getAppContext()?.let { loadUnreadCount(it) }
            }
        }
    }

    fun loadTransmissionMessage(activity: Activity, performerCode: String, isShowLoading: Boolean) {
        val params = HashMap<String, Any>()
        if (performerCode.isNotEmpty())
            params[BUNDLE_KEY.PERFORMER_CODE] = performerCode
        else
            params[BUNDLE_KEY.PERFORMER_CODE] = "0"
        params[BUNDLE_KEY.PAGE] = 1
        params[BUNDLE_KEY.LIMIT] = BUNDLE_KEY.LIMIT_20
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        viewModelScope.launch {
            if (isShowLoading) {
                isLoading.value = true
            }
            try {
                val responseMessage = apiInterface.loadTransmissionMessage(params)
                val transmissionMessageCount = responseMessage.pagination.total
                if (transmissionMessageCount > 0) {
                    isHasTransmissionMessage.value = true
                }
            } catch (throwable: Throwable) {
                if (isShowLoading) {
                    isLoading.value = false
                }
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadTransmissionMessage(activity, performerCode, isShowLoading)
                    }
                )
            } finally {
                isLoading.value = false
            }
        }
    }

    fun openPayMessage(activity: Activity, mailCode: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.openPayMessage(mailCode)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            openPayMessageResult.value = data
                            subtractPoint.value = true
                        }
                    }
                }
            } catch (throwable: Throwable) {
                isLoading.value = false
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        openPayMessage(activity, performerCode)
                    }
                )
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadMessageAfterSend(activity: Activity, performerCode: String) {
        this.isLoadMessageAfterSend = true
        val params = HashMap<String, Any>()
        params[BUNDLE_KEY.PERFORMER_CODE] = performerCode
        params[BUNDLE_KEY.LIMIT] = 1
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        viewModelScope.launch {
            try {
                val responseMessage = apiInterface.loadMessage(params)
                if (responseMessage.errors.isEmpty()) {
                    if (responseMessage.pagination.total >= PERFORMER_MSG_SHOW_REVIEW_APP) {
                        isEnoughMessageForReview.value = true;
                    }
                    if (!responseMessage.dataResponse.isNullOrEmpty()) {
                        val data = responseMessage.dataResponse
                        data.forEach {
                            val time = DateUtil.convertStringToDateString(
                                it.sendDate,
                                DateUtil.DATE_FORMAT_1,
                                DateUtil.DATE_FORMAT_3
                            )
                            if (dataMessageMap.containsKey(time)) {
                                dataMessageMap.get(time)?.add(0, it)
                            } else {
                                val newMap = dataMessageMap.clone()
                                dataMessageMap.clear()
                                val listMessage = mutableListOf<BaseMessageResponse>()
                                listMessage.add(0, it)
                                dataMessageMap[time] = listMessage
                                dataMessageMap.putAll(newMap as LinkedHashMap<String, MutableList<BaseMessageResponse>>)
                            }
                        }
                        val messageResultConvert = mutableListOf<BaseMessageResponse>()
                        for ((key, value) in dataMessageMap) {
                            messageResultConvert.addAll(value)
                            messageResultConvert.add(
                                TimeMessageResponse(
                                    key
                                )
                            )
                        }
                        messageResult.postValue(
                            DataMessage(false, messageResultConvert.reversed())
                        )
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadMessageAfterSend(activity, performerCode)
                    }
                )
            }
        }
    }

    fun sendMessage(messageRequest: MessageRequest, activity: Activity) {
        viewModelScope.launch {
            try {
                isEnableButtonSend.postValue(false)
                val response = apiInterface.sendMessage(messageRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            sendMessageResult.value = data
                            subtractPoint.value = true
                        }
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        sendMessage(messageRequest, activity)
                    }
                )
            } finally {
                isEnableButtonSend.postValue(true)
            }
        }
    }

    fun loadDetailUser(activity: Activity, code: String) {
        if (code.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = apiInterface.getUserProfileDetail(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            userProfileResult.postValue(data)
                        }
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadDetailUser(activity, performerCode)
                    }
                )
            }
        }
    }

    fun sendFreeTemplate(freeTemplateRequest: FreeTemplateRequest, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.sendFreeTemplate(freeTemplateRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            sendMessageResult.value = data
                        }
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    /**
     * handle socket
     */

    fun listenEventSocket() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    fun closeListenEvent() {
        EventBus.getDefault().unregister(this)
    }

    fun loadMemberInfo() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { data ->
                            memberInFoResult.value = data
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    fun resetData() {
        connectResult.value = ConnectResult(result = SocketInfo.RESULT_NONE)
        isLoginSuccess.value = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        responseSocket.value = event
        isMessageFromSocket = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionLoadProfile: ActionLoadProfile) {
        loadMemberInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionReloadMessage: EventBusAction.ReloadMessage) {
        activity?.let {
            loadMessage(it, performerCode, isLoadmore = false, isShowLoading = false)
        }
    }

    private fun loadUnreadCount(context: Context) {
        viewModelScope.launch {
            try {
                val response = apiInterface.getUnreadCount()
                response.let {
                    if (it.errors.isEmpty()) {
                        val unread = it.dataResponse.unread_count
                        rxPreferences.saveNumberUnreadMessage(unread)
                        try {
                            ShortcutBadger.applyCount(context, unread)
                        } catch (e: java.lang.Exception) {
                        }
                    }
                }
            } catch (e: Exception) { // Ignore}
            }
        }
    }

    fun getCurrentConsultant(): ConsultantResponse? {
        return userProfileResult.value
    }

    // Handle for live stream
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
            append("&pass=${rxPreferences.getPassword()}")
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
        flaxWebSocketManager.flaxConnect(urlStartCall, this@ChatMessageViewModel)
    }

    fun connectLiveStream(performerCode: String, status: PerformerStatus) {
        if (viewerStatus == 0 && status == PerformerStatus.WAITING) {
            startCall(performerCode)
        } else {
            connectFlaxChatSocket(performerCode, viewerStatus)
        }
    }

    fun cancelCall(isError: Boolean) {
        connectResult.value = ConnectResult(result = SocketInfo.RESULT_NONE)
        if (!isError) {
            flaxWebSocketManager.cancelCall()
        }
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

    private fun handlePerformerLogin() {
        flaxWebSocketManager.flaxLogout()
        userProfileResult.value?.code?.let { performerCode ->
            connectFlaxChatSocket(performerCode, viewerStatus)
        }
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

    companion object {
        const val HIDDEN_LOAD_MORE = 0
        const val DISABLE_LOAD_MORE = 1
        const val ENABLE_LOAD_MORE = 2
    }
}
