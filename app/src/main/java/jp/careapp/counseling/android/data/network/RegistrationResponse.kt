package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

open class RegistrationResponse : BaseResponse() {

    @SerializedName("token")
    val token: String = ""

    @SerializedName("member_code")
    val memberCode: String = ""

    @SerializedName("token_expire")
    val tokenExpire: String = ""

    @SerializedName("password")
    val passWord: String = ""

    @SerializedName("adid")
    val adid: String = ""
}

class RegistrationWithoutEmailResponse : RegistrationResponse() {
    @SerializedName("email")
    val email: String = ""
}
