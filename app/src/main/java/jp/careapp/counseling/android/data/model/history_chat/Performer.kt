package jp.careapp.counseling.android.data.model.history_chat

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
import java.io.Serializable

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
) : Serializable
