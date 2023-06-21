package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.ui.message.ChatMessageAdapter.Companion.MESSAGE_OWNER
import jp.slapp.android.android.ui.message.ChatMessageAdapter.Companion.MESSAGE_PERFORMER

data class MessageResponse(
    @SerializedName("body")
    val body: String = "",
    @SerializedName("code")
    val code: String = "",
    @SerializedName("exists_attach_file")
    val existsAttachFile: Boolean = false,
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
    @SerializedName("send_mail")
    val sendMail: Boolean = false,
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("pay_flag")
    var payFlag: Boolean = false,
    @SerializedName("pay_preview_count")
    val payPreviewCount: Int = -1,
) : BaseMessageResponse(typeMessage = if (sendMail) MESSAGE_OWNER else MESSAGE_PERFORMER)
