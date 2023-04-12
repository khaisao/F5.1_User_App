package jp.careapp.counseling.android.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavoriteResponse(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("exists_image")
    val existsImage: Boolean = true,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("age")
    val age: Int = 0,
    @SerializedName("stage")
    val stage: Int = 0,
    @SerializedName("bust")
    val bust: Int = 0,
    @SerializedName("ranking")
    val ranking: RankingResponse? = null,
    @SerializedName("recommend_ranking")
    val recommendRanking: Int = 0,
    @SerializedName("is_rookie")
    val isRookie: Int = 0,
    @SerializedName("presence_status")
    val presenceStatus: Int = 0,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("login_member_count")
    val loginMemberCount: Int = 0,
    @SerializedName("peeping_member_count")
    val peepingMemberCount: Int = 0,
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String = "",
    @SerializedName("call_status")
    val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status")
    val chatStatus: Int = DEFAULT_CALL_STATUS,
) : Parcelable
