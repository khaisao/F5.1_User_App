package jp.careapp.counseling.android.network.socket

import jp.careapp.counseling.android.utils.SocketInfo.HEADER_ORIGIN
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.net.URI
import java.security.KeyStore
import java.security.SecureRandom
import java.util.EventListener
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class CallingWebSocketClient {
    companion object {
        const val TAG = "CallingWebSocketClient"
    }

    private lateinit var webSocket: WebSocket
    private var webSocketCallBack: ChatWebSocketCallBack? = null

    fun connect(url: String) {
        Timber.tag(TAG).d("Connecting: $url")
        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers: ${trustManagers.contentToString()}")
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .sslSocketFactory(sslSocketFactory, trustManager)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .addHeader(HEADER_ORIGIN, getOrigin(url))
            .build()

        webSocket = client.newWebSocket(request, getWebSocketListener())
    }

    private fun getOrigin(wsUrl: String): String {
        val uri = URI(wsUrl)
        return if (uri.scheme.equals("wss")) {
            "https://" + uri.authority
        } else {
            "http://" + uri.authority
        }
    }

    fun sendMessage(jsonStr: String) {
        Timber.tag(TAG).d("sendMessage: $jsonStr.")
        webSocket.send(jsonStr)
    }

    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Timber.tag(TAG).e("onClosed: $reason")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Timber.tag(TAG).e("onClosing: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Timber.tag(TAG).e("onFailure: $t.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Timber.tag(TAG).d("onMessage: $text")
                try {
                    val json = JSONObject(text)
                    webSocketCallBack!!.onHandleMessage(json)
                } catch (e: JSONException) {
                    Timber.e("JSONException $e")
                }
            }
        }
    }

    fun closeWebSocket(reason: String = "") {
        webSocket.close(1000, reason)
        Timber.tag(TAG).d("Client close socket")
    }

    fun setCallback(callBack: ChatWebSocketCallBack?) {
        webSocketCallBack = callBack
    }

    interface ChatWebSocketCallBack {
        fun onHandleMessage(jsonMessage: JSONObject)
    }

    interface MaruCastCallBack {
        fun remoteTrackCompleted()
    }

    interface WhisperEventListener : EventListener {
        fun sendWhisperMessage(message: String?)
    }
}