package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName

data class ConsultantOnlineResponse(
    @SerializedName("chat") val listChat: List<ConsultantResponse> = emptyList(),
    @SerializedName("2shot") val listTwoShot: List<ConsultantResponse> = emptyList()
)
