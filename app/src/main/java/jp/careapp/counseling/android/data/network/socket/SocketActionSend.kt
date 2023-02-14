package jp.careapp.counseling.android.data.network.socket

class SocketActionSend(
    val code: String,
    val senderName: String,
    val senderCode: String,
    val subject: String,
    val unreadCount: String,
    val mailCode: String,
    val isAttached: String
) {
    override fun toString(): String {
        return "SocketActionSend(code='$code', senderName='$senderName', senderCode='$senderCode', subject='$subject', unreadCount='$unreadCount', isAttached='$isAttached')"
    }
}
