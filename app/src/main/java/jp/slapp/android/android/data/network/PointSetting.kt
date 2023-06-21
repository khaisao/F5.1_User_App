package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PointSetting(
    @SerializedName("normal_chat_per_minute") val normalChatPerMinute: Int = 0,
    @SerializedName("peeping_per_minute") val peepingPerMinute: Int = 0,
    @SerializedName("2shot_per_minute") val towShotPerMinute: Int = 0,
    @SerializedName("interactive_2shot_per_minute") val interactiveTwoShotPerMinute: Int = 0,
    @SerializedName("whisper") val whisper: Int = 0,
    @SerializedName("mail") val mail: Int = 0,
) : Serializable
