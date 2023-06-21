package jp.slapp.android.android.network.socket

import com.google.gson.Gson
import com.google.gson.JsonObject
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.data.network.socket.SocketActionRead
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.utils.SocketInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class WebSocketClient {
    companion object {
        const val TAG = "WebSocketClient"
    }

    private lateinit var webSocket: WebSocket

    lateinit var token: String

    private var skipOnFail = false


    fun connect(token: String, webSocketListener: WebSocketListener) {
        Timber.tag(TAG).e("connect : $token")
        this.token = token

        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            (
                    "Unexpected default trust managers:" +
                            trustManagers.contentToString()
                    )
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .sslSocketFactory(sslSocketFactory, trustManager)
//            .sslSocketFactory(
//                provideSSLSocketFactory()!!,
//                provideUnTrustManager()[0] as X509TrustManager
//            )
            .build()
        val request: Request = Request.Builder()
            .url(BuildConfig.WS_URL)
            .addHeader(
                SocketInfo.HEADER_PROTOCOL,
                SocketInfo.HEADER_PROTOCOL_VALUE
            )
            .addHeader(SocketInfo.HEADER_ORIGIN, BuildConfig.WS_ORIGIN)
            .build()

        skipOnFail = false
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun reconnect(token: String, webSocketListener: WebSocketListener) {
        if (skipOnFail) {
            return
        }
        GlobalScope.launch {
            delay(timeIntervalRetry)
            timeIntervalRetry =
                (timeIntervalRetry + 1000L) % 20000 // increase time interval after each retry time, max 20000 milisecond = 20 second
            connect(token, webSocketListener = webSocketListener)
        }
    }

    fun authenticationRequest() {
        val data = JsonObject()
        data.addProperty(
            SocketInfo.AUTH_OWN_NAME,
            BuildConfig.WS_OWNER
        )
        data.addProperty(SocketInfo.AUTH_USER, SocketInfo.AUTH_USER_VALUE)
        data.addProperty(SocketInfo.AUTH_TOKEN, token)

        webSocket.send(data.toString())
    }

    fun handleMessage(message: String) {
        var map: Map<String, Any> = HashMap()
        try {
            map = Gson().fromJson(message, map.javaClass)
        } catch (e: Exception) { // ignored}
            return
        }

        when (map["action"]) {
            SocketInfo.ACTION_READ -> {
                EventBus.getDefault().post(Gson().fromJson(message, SocketActionRead::class.java))
            }
            SocketInfo.ACTION_SEND -> {
                EventBus.getDefault().post(Gson().fromJson(message, SocketActionSend::class.java))
            }
        }
    }

    private var timeIntervalRetry = 1000L // 1 second

    fun closeWebSocket() {
        skipOnFail = true
        webSocket.close(1000, "Client close socket")
    }


    private fun provideSSLSocketFactory(): SSLSocketFactory? {
        // Install the all-trusting trust manager
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        try {
            sslContext!!.init(null, provideUnTrustManager(), SecureRandom())
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        // Create an ssl socket factory with our all-trusting manager
        return sslContext!!.socketFactory
    }


    private fun provideUnTrustManager(): Array<TrustManager> {
        return arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
    }
}
