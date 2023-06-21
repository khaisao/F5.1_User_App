package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("member_code")
    val memberCode: String = "",
    @SerializedName("performer_code")
    val performerCode: String = "",
    @SerializedName("point")
    val point: Int = 0,
    @SerializedName("review")
    val review: String = "",
    @SerializedName("status")
    val status: Int = 0
)
