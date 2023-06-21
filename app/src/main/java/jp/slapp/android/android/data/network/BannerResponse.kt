package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName


data class BannerResponse(
    @SerializedName("android_link")
    val androidLink: String?,
    @SerializedName("banner_url")
    val bannerUrl: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("ios_link")
    val iosLink: String?,
    @SerializedName("start_at")
    val startAt: String?,
    @SerializedName("title")
    val title: String?
)