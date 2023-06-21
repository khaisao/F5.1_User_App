package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

class LoginResponse : BaseResponse() {

    @SerializedName("token")
    val token: String = ""

    @SerializedName("member_code")
    val memberCode: String = ""

    @SerializedName("token_expire")
    val tokenExpire: String = ""
}
