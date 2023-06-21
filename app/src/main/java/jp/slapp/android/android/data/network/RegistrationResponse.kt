package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("member_code")
    val memberCode: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("token_expire")
    val tokenExpire: String?,
)
