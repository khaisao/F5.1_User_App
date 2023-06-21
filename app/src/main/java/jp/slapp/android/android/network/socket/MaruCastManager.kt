package jp.slapp.android.android.network.socket

import android.content.Context
import jp.slapp.android.android.data.network.FlaxLoginAuthResponse
import jp.slapp.android.android.utils.SocketInfo.AUDIO_KIND
import jp.slapp.android.android.utils.SocketInfo.KEY_KIND
import jp.slapp.android.android.utils.SocketInfo.KEY_SESSION_CODE
import jp.slapp.android.android.utils.SocketInfo.VIDEO_KIND
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.marge.marucast_android_client.MediaClient
import org.marge.marucast_android_client.event.Event
import org.marge.marucast_android_client.event.LoginEvent
import org.marge.marucast_android_client.media.CameraPosition
import org.marge.marucast_android_client.models.Config
import org.marge.marucast_android_client.models.PresenterRoom
import org.marge.marucast_android_client.models.Resolution
import org.marge.marucast_android_client.views.VideoRendererView
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaruCastManager @Inject constructor(
    private val mediaClient: MediaClient
) : LoginEvent {
    private var isLiveStreamOn: Boolean = false
    private var isPublishing = false
    private var isRoomExists = false
    private var isAudioTrackReady = false
    private var isVideoTrackReady = false
    private var callBack: CallingWebSocketClient.MaruCastCallBack? = null
    private var switchViewerCallback: SwitchViewerCallback? = null
    private var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null

    fun setCallBack(callBack: CallingWebSocketClient.MaruCastCallBack?) {
        this.callBack = callBack
    }

    fun setLiveStreamStateOn(isOn: Boolean) {
        this.isLiveStreamOn = isOn
    }

    fun removeCallBack() {
        callBack = null
    }

    fun setSwitchViewerCallBack(callBack: SwitchViewerCallback?) {
        this.switchViewerCallback = callBack
    }

    fun removeSwitchViewerCallBack() {
        switchViewerCallback = null
    }

    fun loginRoom(flaxLoginAuthResponse: FlaxLoginAuthResponse, context: Context?) {
        this.flaxLoginAuthResponse = flaxLoginAuthResponse
        this.isAudioTrackReady = false
        this.isVideoTrackReady = false

        // マルキャスコンフィグの設定
        val config = Config.Builder(true, true)
            .isCamera2(true)
            .cameraPosition(CameraPosition.FRONT)
            .videoResolution(Resolution.QVGA)
            .build()
        // セッションの初期化処理を行う
        try {
            setMediaClientEventListener()
            mediaClient.initSession(config, context)
            val customUserData = JSONObject()
            customUserData.put(KEY_SESSION_CODE, flaxLoginAuthResponse.sessionCode)
            mediaClient.loginRoom(
                this.flaxLoginAuthResponse!!.mediaServerOwnerCode,
                this.flaxLoginAuthResponse!!.performerCode,
                this.flaxLoginAuthResponse!!.memberCode,
                null,
                flaxLoginAuthResponse.mediaServer,
                false,
                customUserData,
                this
            )
        } catch (ex: java.lang.Exception) {
            Timber.e("" + ex)
        }
    }

    fun logoutRoom() {
        if (!isRoomExists) return
        try {
            mediaClient.logoutRoom()
        } catch (ie: IllegalStateException) {
            Timber.e("already logout")
        }
        setRoomExists(true)
    }

    fun switchCamera() {
        mediaClient.switchCamera()
    }

    fun isPublishing(): Boolean {
        return isPublishing
    }

    fun setRoomExists(flg: Boolean?) {
        isRoomExists = flg!!
    }

    private fun setMediaClientEventListener() {
        mediaClient.setMediaEventListener { mediaEvent: String?, data: Any? ->
            when (mediaEvent) {
                Event.ON_REMOTE_TRACK_READY -> {
                    try {
                        val json = JSONObject(data.toString())
                        val kind = json.getString(KEY_KIND)

                        if (kind == VIDEO_KIND) isVideoTrackReady = true
                        else if (kind == AUDIO_KIND) isAudioTrackReady = true

                        if (!isVideoTrackReady || !isAudioTrackReady) return@setMediaEventListener

                        callBack!!.remoteTrackCompleted()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                else -> Timber.i(mediaEvent)
            }
        }
    }

    fun publishStream(context: Context?) {
        if (!isLiveStreamOn) return
        CoroutineScope((Dispatchers.Main)).launch {
            mediaClient.publishStream(context, switchViewerCallback?.getViewerView())
            isPublishing = true
            switchViewerCallback?.onSwitchViewerGroupVisible(true)
        }
    }

    fun playStream(rendererView: VideoRendererView?) {
        try {
            mediaClient.playStream(rendererView)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun stopPublish() {
        if (!isLiveStreamOn || !isPublishing) return
        Timber.i("すとっぷぱぶりっしゅ")
        CoroutineScope((Dispatchers.Main)).launch {
            try {
                mediaClient.stopPublish()
                isPublishing = false
                switchViewerCallback?.onSwitchViewerGroupVisible(false)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun setMicOff(isOff: Boolean) {
        //To handle error No audio Producer
        try {
            if (isOff) {
                mediaClient.muteAudio()
            } else {
                mediaClient.unmuteAudio()
            }
        } catch (_: Exception) {
        }

    }

    fun setCameraOff(isOff: Boolean) {
        //To handle error No audio Producer
        try {
            if (isOff) {
                mediaClient.muteVideo()
            } else {
                mediaClient.unmuteVideo()
            }
        } catch (_: Exception) {
        }
    }

    override fun onLoginSuccess(presenterRoom: PresenterRoom) {
        Timber.i("ろぐいんせいこう")
        setRoomExists(true)
    }

    override fun onLoginFailure(s: String) {
    }

    interface SwitchViewerCallback {
        fun onSwitchViewerGroupVisible(isVisible: Boolean)
        fun getPresenterView(): VideoRendererView
        fun getViewerView(): VideoRendererView
    }
}