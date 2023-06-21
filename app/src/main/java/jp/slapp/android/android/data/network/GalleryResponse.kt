package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GalleryResponse(
    @SerializedName("image") val image: ImageResponse? =null,
    @SerializedName("thumbnail_image") val thumbnailImage: ThumbnailImageResponse?=null ,
    @SerializedName("comment") val comment: String? ,
) : Serializable


