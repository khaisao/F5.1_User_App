package jp.careapp.counseling.android.data.model.live_stream

import jp.careapp.counseling.android.utils.SocketInfo

data class ConnectResult(
    val result: String = SocketInfo.RESULT_OK,
    val message: String = "",
    val isLogout: Boolean = false,
)