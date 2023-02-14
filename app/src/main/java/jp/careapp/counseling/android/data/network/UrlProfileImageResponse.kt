package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UrlProfileImageResponse (
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String? = "",
) : Serializable