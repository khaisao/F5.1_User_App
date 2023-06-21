package jp.slapp.android.android.ui.top

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.network.NumberNotificationResponse
import jp.slapp.android.android.data.network.socket.SocketActionRead
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.data.network.socket.SocketReconnect
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.network.socket.WebSocketClient
import jp.slapp.android.android.utils.BUNDLE_KEY
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TopViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) :
    BaseViewModel() {

    companion object {
        const val TAG = "TopViewModel"
    }

    val memberInFoResult = MutableLiveData<MemberResponse?>()
    val unReadMessageCount = MutableLiveData<Int?>()
    val newMessage = MutableLiveData<SocketActionSend>()

    val numberNotification = MutableLiveData<ApiObjectResponse<NumberNotificationResponse>>()
    lateinit var socketClient: WebSocketClient

    private var webSocketListener = object : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Timber.tag(TAG).e("onClosed: $reason")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.tag(TAG).e("onClosing: $reason")
            super.onClosing(webSocket, code, reason)
            reConnectSocket()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Timber.tag(TAG).e("onFailure: $t.")
            reConnectSocket()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Timber.tag(TAG).e("onMessage: $text.")
            socketClient.handleMessage(text)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Timber.tag(TAG).e("onOpen: ${response.code} --- ${response.message}")
            socketClient.authenticationRequest()
        }
    }

    private fun getNumberNotification(memberResponse: MemberResponse) {
        val params: MutableMap<String, Any> = HashMap()
        var startTime: String
        val dateTimeCurrent =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT_1))
                .toString()
        params[BUNDLE_KEY.PARAM_CATEGORY] = 1
        if (!memberResponse.newsLastViewDateTime.isNullOrEmpty()) {
            startTime = memberResponse.newsLastViewDateTime
        } else {
            startTime = dateTimeCurrent
        }
        params[BUNDLE_KEY.PARAM_START_AT_FROM] = startTime
        params[BUNDLE_KEY.PARAM_START_AT_TO] = dateTimeCurrent
        viewModelScope.launch {
            try {
                numberNotification.value = apiInterface.getNumberNotification(params)
            } catch (e: Exception) {
            }
        }
    }

    fun loadMemberInfo() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        memberInFoResult.value = it.dataResponse
                        rxPreferences.saveMemberInfo(it.dataResponse)
                        getNumberNotification(it.dataResponse)
                        getCreditPrices(it.dataResponse.firstBuyCredit)
                    }
                }
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    private fun getCreditPrices(firstBuyCredit: Boolean) {
        viewModelScope.launch {
            try {
                apiInterface.getCreditPrices(firstBuyCredit = firstBuyCredit).let {
                    if (it.errors.isEmpty()) {
                        rxPreferences.saveCreditPrices(it.dataResponse)
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).d(e)
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                val response = apiInterface.getUnreadCount()
                response.let {
                    if (it.errors.isEmpty()) {
                        val unread = it.dataResponse.unread_count
                        updateUnreadMessage(unread)
                        rxPreferences.saveNumberUnreadMessage(unread)
                    }
                }
            } catch (e: Exception) { // Ignore}
            }
        }
    }

    fun connectSocket() {
        viewModelScope.launch {
            try {
                val response = apiInterface.getWebSocketToken()
                val token = response.dataResponse.token
                socketClient = WebSocketClient()
                socketClient.connect(token, webSocketListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    fun reConnectSocket() {
        viewModelScope.launch {
            try {
                val response = apiInterface.getWebSocketToken()
                val token = response.dataResponse.token
                socketClient.reconnect(token, webSocketListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun closeSocket() {
        EventBus.getDefault().unregister(this)
        viewModelScope.launch {
            socketClient.closeWebSocket()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketReconnect) {
        closeSocket() // Close old socket
        connectSocket() // Reconnect
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionRead) {
        // To be implemented
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        updateUnreadMessage(event.unreadCount.toInt())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun updateUnreadMessage(unread: Int) {
        unReadMessageCount.value = unread
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNewMessageEvent(event: SocketActionSend) {
        newMessage.value = event
    }
}
