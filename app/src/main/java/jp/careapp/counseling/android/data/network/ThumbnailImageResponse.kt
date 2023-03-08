package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ThumbnailImageResponse(
    @SerializedName("url") val url: String = "",
    @SerializedName("width") val width: Long = 0,
    @SerializedName("height") val height: Long = 0,
) : Serializable
