package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("android")
    val appVersion: String = ""
)