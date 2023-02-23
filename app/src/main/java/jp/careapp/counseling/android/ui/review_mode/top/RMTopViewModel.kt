package jp.careapp.counseling.android.ui.review_mode.top

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.socket.SocketActionRead
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.network.socket.SocketReconnect
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.network.socket.WebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RMTopViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    var isNeedUpdateData = false

    init {
        getMember()
    }

    private fun getMember() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = rmApiInterface.getMember()
                withContext(Dispatchers.Main) {
                    if (response.errors.isEmpty()) {
                        response.dataResponse.let {
                            rxPreferences.setNickName(it.name)
                            rxPreferences.setContent(it.troubleSheetResponse.content)
                            rxPreferences.setPushMail(it.pushMail)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var socketClient: WebSocketClient = WebSocketClient()

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

    fun connectSocket() {
        viewModelScope.launch {
            try {
                val response = rmApiInterface.getWebSocketToken()
                val token = response.dataResponse.token
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
                val response = rmApiInterface.getWebSocketToken()
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
        closeSocket()
        connectSocket()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionRead) {
        // To be implemented
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        // To be implemented
    }

    companion object {
        const val TAG = "RMTopViewModel"
    }
}