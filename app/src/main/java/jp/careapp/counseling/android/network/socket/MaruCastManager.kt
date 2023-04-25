package jp.careapp.counseling.android.network.socket

import android.app.Activity
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.utils.SocketInfo.KEY_SESSION_CODE
import org.json.JSONObject
import org.marge.marucast_android_client.MediaClient
import org.marge.marucast_android_client.event.Event
import org.marge.marucast_android_client.event.LoginEvent
import org.marge.marucast_android_client.media.CameraPosition
import org.marge.marucast_android_client.models.Config
import org.marge.marucast_android_client.models.PresenterRoom
import org.marge.marucast_android_client.views.VideoRendererView
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaruCastManager @Inject constructor(
    private val mediaClient: MediaClient
) : LoginEvent {
    private var activity: Activity? = null
    private var isMute = false
    private var isPublishing = false
    private var isRoomExists = false
    private var loginCallBack: CallingWebSocketClient.MaruCastLoginCallBack? = null
    private var switchViewerCallback: SwitchViewerCallback? = null
    private var flaxLoginAuthResponse: FlaxLoginAuthResponse? = null

    fun setLoginCallBack(callBack: CallingWebSocketClient.MaruCastLoginCallBack?) {
        this.loginCallBack = callBack
    }

    fun removeLoginCallBack() {
        loginCallBack = null
    }

    fun setSwitchViewerCallBack(callBack: SwitchViewerCallback?) {
        this.switchViewerCallback = callBack
    }

    fun removeSwitchViewerCallBack() {
        switchViewerCallback = null
    }

    fun connectServer(flaxLoginAuthResponse: FlaxLoginAuthResponse) {
        this.flaxLoginAuthResponse = flaxLoginAuthResponse
        try {
            setMediaClientEventListener()
            mediaClient.connectServer(
                flaxLoginAuthResponse.mediaServerOwnerCode,
                flaxLoginAuthResponse.mediaServer
            )
        } catch (e: Exception) {
            Timber.e("サーバーへの接続に失敗しました$e")
        }
    }

    fun logoutRoom() {
        //To handler log out when performer log out livestream
        try {
            if (!isRoomExists) return
            mediaClient.logoutRoom()
        } catch (e: Exception) {
        }
    }

    fun handleConnected(activity: Activity?) {
        this.activity = activity

        // マルキャスコンフィグの設定
        val config = Config.Builder(true, true)
            .isCamera2(true)
            .cameraPosition(CameraPosition.FRONT)
            .build()
        // セッションの初期化処理を行う
        try {
            mediaClient.initSession(config, this.activity)
            val customUserData = JSONObject()
            customUserData.put(KEY_SESSION_CODE, flaxLoginAuthResponse!!.sessionCode)
            mediaClient.loginRoom(
                flaxLoginAuthResponse!!.mediaServerOwnerCode,
                flaxLoginAuthResponse!!.performerCode,
                flaxLoginAuthResponse!!.memberCode,
                false,
                customUserData,
                this
            )
        } catch (ex: java.lang.Exception) {
            Timber.e("" + ex)
        }
    }

    fun switchCamera() {
        mediaClient.switchCamera()
    }

    fun isPublishing(): Boolean? {
        return isPublishing
    }

    fun isMute(): Boolean? {
        return isMute
    }

    fun setRoomExists(flg: Boolean?) {
        isRoomExists = flg!!
    }

    private fun setMediaClientEventListener() {
        mediaClient.setMediaEventListener { mediaEvent: String?, _: Any? ->
            when (mediaEvent) {
                Event.ON_CONNECT ->
                    // 接続に成功
                    loginCallBack!!.loginSuccess()
                else -> Timber.i(mediaEvent!!)
            }
        }
    }

    fun publishStream() {
        if (activity == null) return
        activity?.runOnUiThread {
            mediaClient.publishStream(activity?.baseContext, switchViewerCallback?.getViewerView())
            isPublishing = true
            switchViewerCallback?.onSwitchViewerGroupVisible(true)
        }
        isMute = false
    }

    fun stopPublish() {
        if (activity == null || !isPublishing) return
        Timber.i("すとっぷぱぶりっしゅ")
        activity!!.runOnUiThread {
            try {
                mediaClient.stopPublish()
                isPublishing = false
                switchViewerCallback?.onSwitchViewerGroupVisible(false)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun muteStream() {
        isMute = !isMute
        Timber.i(isMute.toString())
        if (isMute) {
            mediaClient.muteAudio()
            mediaClient.muteVideo()
        } else {
            mediaClient.unmuteAudio()
            mediaClient.unmuteVideo()
        }
    }

    override fun onLoginSuccess(presenterRoom: PresenterRoom) {
        Timber.i("ろぐいんせいこう")
        setRoomExists(true)
        activity!!.runOnUiThread { mediaClient.playStream(switchViewerCallback?.getPresenterView()) }
    }

    override fun onLoginFailure(s: String) {
        activity!!.runOnUiThread {
            switchViewerCallback?.getPresenterView()?.release()
        }
    }

    interface SwitchViewerCallback {
        fun onSwitchViewerGroupVisible(isVisible: Boolean)
        fun getPresenterView(): VideoRendererView
        fun getViewerView(): VideoRendererView
    }
}