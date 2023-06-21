package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RankingResponse(
    @SerializedName("ranking") val ranking: Int = 0,
    @SerializedName("point") val point: Int = 0,
    @SerializedName("interval") val interval: Int = 0,
) : Serializable
