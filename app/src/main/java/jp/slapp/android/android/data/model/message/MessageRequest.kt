package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("performer_code")
    var performerCode: String = "",
    @SerializedName("subject")
    var subject: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("mail_code")
    var mailCode: String = "",
)
