package jp.careapp.counseling.android.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
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
    @SerializedName("last_login_date")
    val lastLoginDate: Date?,
    @SerializedName("member_last_login_date")
    val memberLastLoginDate: Date?,
) : Parcelable
