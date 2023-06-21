package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FreeTemplateResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("body") val body: String
) : Serializable
