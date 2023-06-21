package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class FssMemberAuthResponse(
    @SerializedName("name")
    val name: String?,
    @SerializedName("token")
    val token: String?
)
