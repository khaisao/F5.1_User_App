package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

class WebsocketResponse : BaseResponse() {

    @SerializedName("token")
    val token: String = ""
}
