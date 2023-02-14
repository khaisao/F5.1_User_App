package jp.careapp.core.model.network

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("count")
    val count: Int = -1
)
