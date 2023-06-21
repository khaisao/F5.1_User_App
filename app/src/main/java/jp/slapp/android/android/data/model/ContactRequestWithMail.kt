package jp.slapp.android.android.data.model

import com.google.gson.annotations.SerializedName

data class ContactRequestWithMail(
    @SerializedName("category")
    var category: String = "",
    @SerializedName("content")
    var content: String = "",
    @SerializedName("reply")
    var reply: Int = 0,
    @SerializedName("email")
    var email: String = ""
)
