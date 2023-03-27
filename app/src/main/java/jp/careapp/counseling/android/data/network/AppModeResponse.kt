package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.utils.Define.Companion.NORMAL_MODE

data class AppModeResponse(
    @SerializedName("app_mode")
    val appMode: Int = NORMAL_MODE
)