package jp.careapp.counseling.android.data.model.message

import com.google.gson.annotations.SerializedName

data class RMMessageRequest(
    @SerializedName("performer_code") var memberCode: String = "",
    @SerializedName("message") var message: String = ""
)
