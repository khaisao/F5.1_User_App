package jp.careapp.counseling.android.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TroubleSheetResponse(
    @SerializedName("content") val content: String = "",
    @SerializedName("response") val response: Int = 0,
    @SerializedName("reply") val reply: Int = 0,
    @SerializedName("genre") val genre: Int? = 0,
    @SerializedName("partner_name") val partnerName: String? = "",
    @SerializedName("partner_sex") val partnerSex: Int? = 0,
    @SerializedName("partner_birth") val partnerBirth: String? = "",
) : Parcelable
