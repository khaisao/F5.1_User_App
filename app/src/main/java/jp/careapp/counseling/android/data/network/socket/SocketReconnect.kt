package jp.careapp.counseling.android.data.network.socket

class SocketReconnect(
    val code: String
) {
    override fun toString(): String {
        return "SocketReconnect(code='$code')"
    }
}
