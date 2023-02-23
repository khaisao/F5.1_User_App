package jp.careapp.counseling.android.data.model.message

import com.google.gson.annotations.SerializedName

data class RMPerformer(
    @SerializedName("code")
    val code: String? = "",
    @SerializedName("exists_image")
    val existsImage: Boolean? = false,
    @SerializedName("image_url")
    val imageUrl: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("presence_status")
    val presenceStatus: Int? = 0,
    @SerializedName("status")
    val status: Int? = 0,
    @SerializedName("call_status")
    val callStatus: Int? = 0,
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String? = ""
)
