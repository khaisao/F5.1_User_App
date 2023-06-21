package jp.slapp.android.android.data.model.live_stream

import jp.slapp.android.android.utils.SocketInfo

data class ConnectResult(
    val result: String = SocketInfo.RESULT_OK,
    val message: String = "",
    val isLogout: Boolean = false,
)