package jp.slapp.android.android.data.network

open class ErrorResponse(
    var timestamp: String?,
    var status: Int?,
    var error: String?,
    var message: String?,
    var path: String?
)
