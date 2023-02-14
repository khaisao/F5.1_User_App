package jp.careapp.counseling.android.data.network.socket

class SocketActionRead(
    val code: String,
    val mailCode: String,
) {
    override fun toString(): String {
        return "SocketActionRead(code='$code', mailCode='$mailCode')"
    }
}
