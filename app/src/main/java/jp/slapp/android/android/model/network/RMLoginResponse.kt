package jp.slapp.android.android.model.network

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.data.network.BaseResponse

class RMLoginResponse : BaseResponse() {

    @SerializedName("token")
    var token: String = ""

    @SerializedName("member_code")
    var memberCode: String = ""

    @SerializedName("token_expire")
    var tokenExpire: String = ""
}