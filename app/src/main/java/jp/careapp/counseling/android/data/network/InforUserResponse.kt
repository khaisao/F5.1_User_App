package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

class InforUserResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("token") val token: String,
    @SerializedName("token_expire") val tokenExpire: String,
    @SerializedName("member_code") val memberCode: String,
    @SerializedName("password") val password: String
)
