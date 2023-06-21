package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class ApiObjectResponse<T>(
    @SerializedName("errors") var errors: List<String>,
    @SerializedName("data") var dataResponse: T,
    @SerializedName("pagination") var pagination: PaginationResponse
)

data class ApiException(
    @SerializedName("errors") var errors: List<String>,
    @SerializedName("data") var dataResponse: Any,
    @SerializedName("pagination") var pagination: PaginationResponse
)
