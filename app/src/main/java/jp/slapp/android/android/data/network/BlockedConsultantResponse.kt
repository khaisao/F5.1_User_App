package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class BlockedConsultantResponse(
    @SerializedName("code") val code: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("exists_image") val existsImage: Boolean = false,
    @SerializedName("image_url") val imageUrl: String = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String = "",
    @SerializedName("presence_status") val presenceStatus: Int = 0,
    @SerializedName("status") val status: Int = 0
)
