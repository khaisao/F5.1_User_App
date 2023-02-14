package jp.careapp.counseling.android.data.model.user_profile

data class TroubleSheetRequest(
    var content: String = "",
    var response: Int = -1,
    var reply: Int = -1
)
