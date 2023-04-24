package jp.careapp.counseling.android.data.model

import com.google.gson.annotations.SerializedName

data class ContactRequestWithoutMail(
    @SerializedName("category")
    var category: String = "",
    @SerializedName("content")
    var content: String = "",
    @SerializedName("reply")
    var reply: Int = 0
)