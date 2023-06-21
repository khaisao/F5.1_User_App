package jp.slapp.android.android.data.model

import com.google.gson.annotations.SerializedName

data class NewQuestionRequest(
    @SerializedName("counselee_name") val counseleeName: String,
    @SerializedName("genre") val genre: Int,
    @SerializedName("body") val body: String
)