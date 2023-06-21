package jp.slapp.android.android.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastBuyLog(
    @SerializedName("buy_date") val buyDate: String?,
    @SerializedName("payway") val payWay: Int?,
    @SerializedName("point") val point: Int?,
    @SerializedName("price") val price: Int?,
    @SerializedName("status") val status: Int?,
    @SerializedName("tax") val tax: Int?
) : Parcelable