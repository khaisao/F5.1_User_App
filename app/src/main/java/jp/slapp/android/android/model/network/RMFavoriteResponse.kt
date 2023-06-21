package jp.slapp.android.android.model.network

import com.google.gson.annotations.SerializedName

data class RMFavoriteResponse(
    @SerializedName("code")
    val code: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("exists_image")
    val existsImage: Boolean? = false,
    @SerializedName("image_url")
    val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String? = "",
    @SerializedName("presence_status")
    val presenceStatus: Int? = 0,
    @SerializedName("call_status")
    val callStatus: Int? = 0,
    @SerializedName("status")
    val status: Int? = 0,
    @SerializedName("chat_status")
    val chatStatus: Int?
) : BasePerformerResponse()
