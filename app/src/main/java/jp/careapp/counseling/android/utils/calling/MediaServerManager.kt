package jp.careapp.counseling.android.utils.calling

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import jp.careapp.core.utils.SafeCallApi.gson
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import org.json.JSONObject
import org.marge.marucast_android_client.MediaClient
import org.marge.marucast_android_client.event.Event
import org.marge.marucast_android_client.event.LoginEvent
import org.marge.marucast_android_client.models.Config
import org.marge.marucast_android_client.models.PresenterRoom
import timber.log.Timber

const val TAG = "MediaServerManager"

class MediaServerManager constructor(
    private val context: Context
) {
    private val mediaClient by lazy { MediaClient() }
    private var onPublishSuccess: (() -> Unit)? = null
    private val mediaServerThread = HandlerThread("MediaServerThread").apply { start() }
    private var handler: Handler = Handler(mediaServerThread.looper)
    private var isPublishing = false
    private var isRoomExists = false

    fun loginToRoom(
        flaxLoginAuthResponse: FlaxLoginAuthResponse,
        loginToRoomSuccess: (() -> Unit)? = null
    ) {
        try {
            mediaClient.setMediaEventListener { mediaEvent: String?, data: Any? ->
                when (mediaEvent) {
                    Event.ON_PUBLISH_STATUS_CHANGE -> {
                        gson.fromJson(gson.toJson(data), EventData::class.java)?.apply {
                            if (mStatus.equals("success")) {
                                onPublishSuccess?.invoke()
                            }
                        }
                    }
                }
                Timber.tag(TAG).i(mediaEvent)
            }

            val config = Config.Builder(false, true).build()
            mediaClient.initSession(config, context)
            val customUserData = JSONObject()
            customUserData.put("sessionCode", flaxLoginAuthResponse.sessionCode)
            mediaClient.loginRoom(
                flaxLoginAuthResponse.mediaServerOwnerCode,
                flaxLoginAuthResponse.performerCode,
                flaxLoginAuthResponse.memberCode,
                "",
                flaxLoginAuthResponse.mediaServer,
                false,
                customUserData,
                object : LoginEvent {
                    override fun onLoginSuccess(presenterRoom: PresenterRoom) {
                        Timber.tag(TAG).i("Login success")
                        isRoomExists = true
                        handler.post {
                            mediaClient.playStream(null)
                            loginToRoomSuccess?.invoke()
                        }
                    }

                    override fun onLoginFailure(error: String) {
                        Timber.tag(TAG).e(error)
                    }
                }
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    fun setOnPublishSuccessListener(onPublishSuccess: () -> Unit) {
        this.onPublishSuccess = onPublishSuccess
    }

    fun logoutRoom() {
        if (!isRoomExists) return
        try {
            mediaClient.logoutRoom()
            mediaServerThread.quitSafely()
            isRoomExists = false
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    fun publishStream() {
        try {
            handler.post {
                mediaClient.publishStream(context)
                isPublishing = true
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    fun stopPublish() {
        if (!isPublishing) return
        Timber.tag(TAG).i("Stop publish")
        handler.post {
            try {
                mediaClient.stopPublish()
                isPublishing = false
            } catch (e: InterruptedException) {
                Timber.tag(TAG).e(e)
            }
        }
    }

    fun muteAudio(isMute: Boolean) {
        if (!isPublishing) return
        try {
            if (isMute) {
                mediaClient.muteAudio()
            } else {
                mediaClient.unmuteAudio()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}

data class EventData(
    val userId: String? = "",
    val kind: String? = "",
    val mInterval: Int? = 0,
    val mKind: String? = "",
    val mStatus: String? = ""
)