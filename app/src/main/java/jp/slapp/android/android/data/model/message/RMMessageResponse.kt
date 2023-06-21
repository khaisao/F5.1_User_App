package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.ui.review_mode.user_detail_message.RMUserDetailMsgAdapter.Companion.MESSAGE_OWNER
import jp.slapp.android.android.ui.review_mode.user_detail_message.RMUserDetailMsgAdapter.Companion.MESSAGE_PERFORMER

data class RMMessageResponse(
    @SerializedName("body")
    val body: String? = "",
    @SerializedName("code")
    val code: String? = "",
    @SerializedName("exists_attach_file")
    val existsAttachFile: Boolean? = false,
    @SerializedName("from_owner_mail")
    val fromOwnerMail: Boolean? = false,
    @SerializedName("image_string")
    val imageString: String? = "",
    @SerializedName("open")
    var open: Boolean? = false,
    @SerializedName("performer")
    val performer: RMPerformer? = null,
    @SerializedName("return")
    val returnX: Boolean? = false,
    @SerializedName("send_mail")
    val sendMail: Boolean? = false,
    @SerializedName("subject")
    val subject: String? = "",
    @SerializedName("pay_flag")
    var payFlag: Boolean? = false,
    @SerializedName("pay_preview_count")
    val payPreviewCount: Int? = -1,
) : BaseMessageResponse(typeMessage = if (sendMail == true) MESSAGE_OWNER else MESSAGE_PERFORMER)
