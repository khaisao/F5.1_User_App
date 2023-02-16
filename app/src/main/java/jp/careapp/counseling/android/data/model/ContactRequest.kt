package jp.careapp.counseling.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactRequest(
    @SerializedName("category")
    var category: String = "",
    @SerializedName("content")
    var content: String = "",
    @SerializedName("reply")
    var reply: Int = 0,
    @SerializedName("email")
    var email: String = ""
) : Parcelable

@Parcelize
data class ContactRequestWithoutMail(
    @SerializedName("category")
    var category: String = "",
    @SerializedName("content")
    var content: String = "",
    @SerializedName("reply")
    var reply: Int = 0,
    @SerializedName("email")
    var email: String = ""
) : Parcelable