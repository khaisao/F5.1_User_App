package jp.careapp.counseling.android.data.network.socket

data class SocketSendMessage(
    var action: String? = "",
    var shotStatus: Int? = null
)

open class BaseSocketReceiveMessage {
    var action: String = ""
    var result: String = ""
    var message: String = ""
    var error: String = ""
    var errorStatus: Int = 0
    var errorObject: ErrorObject? = null

    fun isResultOK(): Boolean {
        return result != "NG"
    }

    data class ErrorObject(
        var message: String = "",
        var status: Int = 0
    )
}

data class LoginRequestMessage(
    var isNeedCall: Boolean
) : BaseSocketReceiveMessage()

data class LoginMemberParam(
    val ownerName: String,
    val performerCode: String,
    val token: String,
    val status: Int
)

data class LoginMessage(
    val chips: List<Chip> = emptyList(),
    val loginCode: String = "",
    val mediaServer: String = "",
    val mediaServerOwnerCode: String = "",
    val memberCode: String = "",
    val performerCode: String = "",
    val performerThumbnailImage: String = "",
    val pointWarningMessage: String = "",
    val pointWarningPoint: String = "",
    val remainingFreeTime: String = "",
    val rows: Int = 0,
    val sessionCode: String = "",
    val status: String = "",
    val timeout: String = "",
    val turnCredential: String = "",
    val turnUrl: String = "",
    val turnUsername: String = ""
) : BaseSocketReceiveMessage()

data class Chip(
    val label: String = "",
    val point: String = ""
)

data class ChatLogMessage(
    val chat: List<Any> = emptyList(),
    val chipStatus: String = "",
    val mainMemberCode: String = "",
    val point: String = "",
    val twoshot: String = "",
    val twoshotTime: String = "",
    val whisper: List<Any> = emptyList(),
    val peepingCount: String = "",
    val memberCount: String = "",
    val remainingFreeTime: String = ""
) : BaseSocketReceiveMessage()


