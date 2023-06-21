package jp.slapp.android.android.data.model.history_chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HistoryChatResponse(
    @SerializedName("body")
    val body: String = "",
    @SerializedName("code")
    val code: String = "",
    @SerializedName("exists_atatach_file")
    val existsAtatachFile: Boolean = false,
    @SerializedName("from_owner_mail")
    val fromOwnerMail: Boolean = false,
    @SerializedName("image_string")
    val imageString: String = "",
    @SerializedName("open")
    var open: Boolean = false,
    @SerializedName("performer")
    val performer: Performer? = null,
    @SerializedName("return")
    val returnX: Boolean = false,
    @SerializedName("send_date")
    val sendDate: String = "",
    @SerializedName("send_mail")
    val sendMail: Boolean = false,
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("pay_flag")
    val payFlag: Boolean = false,
    @SerializedName("pay_preview_count")
    val payPreviewCount: Int = -1,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    var payOpen: Int = 0
) : Serializable
