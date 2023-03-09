package jp.careapp.counseling.android.data.model.history_chat

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.RankingResponse
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
import jp.careapp.counseling.android.utils.ChatStatus.DEFAULT_CHAT_STATUS
import java.io.Serializable

data class Performer(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("age")
    val age: Int = 0,
    @SerializedName("bust")
    val bust: Int = 0,
    @SerializedName("exists_image")
    val existsImage: Boolean = false,
    @SerializedName("stage")
    val stage: Int = 0,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String = "",
    @SerializedName("ranking")
    val ranking: RankingResponse? = null,
    @SerializedName("recommend_ranking")
    val recommendRanking: Int = 0,
    @SerializedName("presence_status")
    val presenceStatus: Int = 0,
    @SerializedName("call_status")
    val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status")
    val chatStatus: Int = DEFAULT_CHAT_STATUS,
    @SerializedName("status")
    val status: Int = 0,
) : Serializable
