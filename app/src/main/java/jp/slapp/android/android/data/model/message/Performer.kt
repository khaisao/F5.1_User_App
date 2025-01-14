package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.utils.CallStatus.DEFAULT_CALL_STATUS

data class Performer(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("exists_image")
    val existsImage: Boolean = false,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("presence_status")
    val presenceStatus: Int = 0,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String = "",
    @SerializedName("call_status")
    val callStatus: Int = DEFAULT_CALL_STATUS,
)
