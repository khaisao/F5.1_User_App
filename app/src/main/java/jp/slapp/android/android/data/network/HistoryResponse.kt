package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.utils.CallStatus.DEFAULT_CALL_STATUS
import java.io.Serializable
import java.util.*

data class HistoryResponse(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("age")
    val age: Int = 0,
    @SerializedName("bust")
    var bust: Int = 0,
    @SerializedName("exists_image")
    val existsImage: Boolean = true,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String = "",
    @SerializedName("message_of_the_day")
    val messageOfTheDay: String?,
    @SerializedName("call_status")
    val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status")
    val chatStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("last_login_date")
    val lastLoginDate: String?,
    @SerializedName("member_last_login_date")
    val memberLastLoginDate: String?,
) : Serializable
