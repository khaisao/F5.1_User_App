package jp.careapp.counseling.android.data.model.message

import com.google.gson.annotations.SerializedName

data class SendMessageResponse(
    @SerializedName("point")
    val point: Int = 0
)
