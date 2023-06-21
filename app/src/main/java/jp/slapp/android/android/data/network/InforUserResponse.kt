package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InfoUserResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("token") val token: String?,
    @SerializedName("token_expire") val tokenExpire: String?,
    @SerializedName("member_code") val memberCode: String?,
    @SerializedName("password") val password: String?
): Serializable
