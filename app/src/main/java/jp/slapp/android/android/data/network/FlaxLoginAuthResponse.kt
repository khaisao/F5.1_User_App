package jp.slapp.android.android.data.network

import java.io.Serializable

data class FlaxLoginAuthResponse(
    val memberCode: String = "",
    val performerCode: String = "",
    val mediaServerOwnerCode: String = "",
    val mediaServer: String = "",
    val sessionCode: String = "",
    val performerThumbnailImage: String = "",
    val loginType: Int = 0
) : Serializable
