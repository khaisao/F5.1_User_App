package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.data.model.history_chat.Performer
import java.io.Serializable

data class AnswerResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("body") val body: String? = "",
    @SerializedName("is_best") val isBest: Boolean = false,
    @SerializedName("created_at") val createAt: String? = "",
    @SerializedName("performer") val performer: Performer = Performer(),
    var isItemReport: Boolean = false
) : Serializable
