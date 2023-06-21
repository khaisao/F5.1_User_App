package jp.slapp.android.android.data.network

data class ConfigCallResponse(
    val fssAddMemberFavorite: String?,
    val fssGetPointSetting: String?,
    val fssMemberAppAuthUrl: String?,
    val fssPerformerInfo: String?,
    val fssRemoveMemberFavorite: String?,
    val ownerCode: String?,
    val ownerName: String?,
    val wsMemberLoginRequest: String?
)

fun defaultConfigCall(): ConfigCallResponse {
    return ConfigCallResponse("", "", "", "", "", "", "", "")
}