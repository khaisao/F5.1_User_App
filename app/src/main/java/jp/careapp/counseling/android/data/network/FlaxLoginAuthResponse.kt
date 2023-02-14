package jp.careapp.counseling.android.data.network

data class FlaxLoginAuthResponse(
    val memberCode: String = "",
    val performerCode: String = "",
    val mediaServerOwnerCode: String = "",
    val mediaServer: String = "",
    val sessionCode: String = "",
    val performerThumbnailImage: String = "",
)
