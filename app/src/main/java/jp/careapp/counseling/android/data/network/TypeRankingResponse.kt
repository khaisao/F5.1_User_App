package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

data class TypeRankingResponse(
    @SerializedName("ranking") val ranking: Int = 0,
    @SerializedName("point") val point: Int = 0,
    @SerializedName("performer") val performerResponse: ConsultantResponse? = null,
    @SerializedName("recommend_ranking") val recommendRanking: Int? = null
)
