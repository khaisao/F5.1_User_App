package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

class PaginationResponse(
    @SerializedName("total") var total: Int,
    @SerializedName("page") var page: Int,
    @SerializedName("limit") var limit: Int
)
