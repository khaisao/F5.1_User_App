package jp.careapp.counseling.android.network.socket

import jp.careapp.counseling.android.utils.Define.Companion.FLAX_STATUS_PREMIUM_PRIVATE
import jp.careapp.counseling.android.utils.Define.Companion.FLAX_STATUS_PRIVATE
import jp.careapp.counseling.android.utils.SocketInfo
import jp.careapp.counseling.android.utils.SocketInfo.ACTION_CHANGE_TWO_SHOT_STATUS
import jp.careapp.counseling.android.utils.SocketInfo.KEY_ACTION
import jp.careapp.counseling.android.utils.SocketInfo.KEY_COLOR
import jp.careapp.counseling.android.utils.SocketInfo.KEY_MESSAGE_TYPE
import jp.careapp.counseling.android.utils.SocketInfo.KEY_MSG
import jp.careapp.counseling.android.utils.SocketInfo.KEY_SHOT_STATUS
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlaxWebSocketManager @Inject constructor() {

    private var socketClient: CallingWebSocketClient? = null

    fun flaxConnect(url: String, callBack: CallingWebSocketClient.ChatWebSocketCallBack?) {
        socketClient = CallingWebSocketClient()
        socketClient?.setCallback(callBack)
        socketClient?.connect(url)
    }

    fun setCallback(callBack: CallingWebSocketClient.ChatWebSocketCallBack?) {
        socketClient?.setCallback(callBack)
    }

    fun flaxLogout() {
        socketClient?.closeWebSocket()
    }

    fun cancelCall() {
        val json = JSONObject()
        try {
            json.put(KEY_ACTION, SocketInfo.ACTION_CANCEL_CALL)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun changeStatus() {
        val json = JSONObject()
        try {
            json.put(KEY_ACTION, SocketInfo.ACTION_CHANGE_STATUS)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendMessage(message: String) {
        val json = JSONObject()
        try {
            json.put(KEY_ACTION, SocketInfo.ACTION_MESSAGE)
            json.put(KEY_MESSAGE_TYPE, SocketInfo.ACTION_WRITE)
            json.put(KEY_MSG, message)
            json.put(KEY_COLOR, SocketInfo.MESSAGE_COLOR)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendWhisperMessage(message: String) {
        if (message.isEmpty()) return
        val json = JSONObject()
        try {
            json.put(KEY_ACTION, SocketInfo.ACTION_MESSAGE)
            json.put(KEY_MESSAGE_TYPE, SocketInfo.ACTION_WHISPER)
            json.put(KEY_MSG, message)
            json.put(KEY_COLOR, SocketInfo.MESSAGE_COLOR)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun privateModeInvitation() {
        try {
            val json = JSONObject()
            json.put(KEY_ACTION, SocketInfo.ACTION_TWO_SHOT_REQUEST)
            json.put(KEY_SHOT_STATUS, FLAX_STATUS_PRIVATE)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun privateModeCancel() {
        try {
            val json = JSONObject()
            json.put(KEY_ACTION, SocketInfo.ACTION_CANCEL_TWO_SHOT)
            json.put(KEY_SHOT_STATUS, FLAX_STATUS_PRIVATE)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun privateModeDestroy() {
        try {
            val json = JSONObject()
            json.put(KEY_ACTION, SocketInfo.ACTION_DESTROY_TWO_SHOT)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun premiumPrivateModeInvitation() {
        val json = JSONObject()
        try {
            json.put(KEY_ACTION, ACTION_CHANGE_TWO_SHOT_STATUS)
            json.put(KEY_SHOT_STATUS, FLAX_STATUS_PREMIUM_PRIVATE)
            socketClient?.sendMessage(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}