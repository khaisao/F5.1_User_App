package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class RegistrationWithoutEmailResponse(
    @SerializedName("member_code")
    val memberCode: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("token_expire")
    val tokenExpire: String?,
)
