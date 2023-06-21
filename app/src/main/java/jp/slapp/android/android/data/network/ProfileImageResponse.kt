package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImageResponse (
    @SerializedName("1") val imagePatternOne: UrlProfileImageResponse? = null,
    @SerializedName("2") val imagePatternTwo: UrlProfileImageResponse? = null,
) : Serializable