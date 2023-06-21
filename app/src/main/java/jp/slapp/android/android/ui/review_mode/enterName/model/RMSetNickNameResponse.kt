package jp.slapp.android.android.ui.review_mode.enterName.model

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.data.network.BaseResponse

class RMSetNickNameResponse : BaseResponse() {
    @SerializedName("member_code")
    val memberCode: String? = ""

    @SerializedName("email")
    val email: String? = ""

    @SerializedName("password")
    val password: String? = ""

    @SerializedName("token")
    val token: String? = ""

    @SerializedName("token_expire")
    val tokenExpire: String? = ""

    @SerializedName("adid")
    val adid: String? = ""
}