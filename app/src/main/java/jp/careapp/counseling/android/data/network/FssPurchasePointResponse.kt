package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

data class FssPurchasePointResponse(
    @SerializedName("set1")
    val set1: FssPointSetResponse,
    @SerializedName("set2")
    val set2: FssPointSetResponse,
    @SerializedName("set3")
    val set3: FssPointSetResponse,
)

data class FssPointSetResponse(
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("point")
    val point: Int = 0,
    @SerializedName("bonus")
    val bonus: Int = 0
)
