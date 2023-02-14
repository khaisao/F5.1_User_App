package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

data class NumberNotificationResponse(
    @SerializedName("count") var count: Int = 0
)
