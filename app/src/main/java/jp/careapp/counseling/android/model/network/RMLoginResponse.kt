package jp.careapp.counseling.android.model.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.data.network.BaseResponse

class RMLoginResponse : BaseResponse() {

    @SerializedName("token")
    var token: String? = ""

    @SerializedName("member_code")
    var memberCode: String? = ""

    @SerializedName("token_expire")
    var tokenExpire: String? = ""
}