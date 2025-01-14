package jp.slapp.android.android.data.network

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.data.model.history_chat.Performer
import jp.slapp.android.android.utils.CallStatus.DEFAULT_CALL_STATUS
import java.io.Serializable

data class ConsultantResponse(
    @SerializedName("code") val code: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("sex") val sex: Int = 3,
    @SerializedName("age") val age: Int = 0,
    @SerializedName("bust") val bust: Int = 0,
    @SerializedName("favorite_member_count") val favoriteMemberCount: Int = 0,
    @SerializedName("exists_image") val existsImage: Boolean = false,
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("message_of_the_day") val messageOfTheDay: String? = "",
    @SerializedName("message_notice") val messageNotice: String? = "",
    @SerializedName("genres") val genres: List<Int> = listOf(),
    @SerializedName("stage") val stage: Int = 0,
    @SerializedName("review_average") val reviewAverage: Double = 0.0,
    @SerializedName("review_score") val reviewScore: Int = 0,
    @SerializedName("review_total_number") val reviewTotalNumber: Int = 0,
    @SerializedName("review_total_score") val reviewTotalScore: Int = 0,
    @SerializedName("point_per_char") val pointPerChar: Int = 0,
    @SerializedName("point_setting") val pointSetting: PointSetting? = null,
    @SerializedName("login_plans_datetime") val loginPlansDatetime: String? = "",
    @SerializedName("ranking") val ranking: RankingResponse? = null,
    @SerializedName("recommend_ranking") val recommendRanking: Int = 0,
    @SerializedName("is_rookie") val isRookie: Int = 0,
    @SerializedName("profile_pattern") val profilePattern: Int = 0,
    @SerializedName("profile_images") val profileImages: ProfileImageResponse? = null,
    @SerializedName("presence_status") val presenceStatus: Int = 0,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("login_member_count") val loginMemberCount: Int = 0,
    @SerializedName("peeping_member_count") val peepingMemberCount: Int = 0,
    @SerializedName("is_favorite") var isFavorite: Boolean = false,
    @SerializedName("point_per_minute") val pointPerMinute: Int = 0,
    @SerializedName("call_status") val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status") val chatStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("call_restriction") val callRestriction: Int? = 0, // 1=possible, null=not possible
    @SerializedName("is_blocked") val isBlocked: Boolean = false,
) : Serializable {
    companion object {
        const val PREVIOUS = 0
        const val WEEK = 1
        const val MONTH = 2

        fun from(performer: Performer): ConsultantResponse {
            return ConsultantResponse(
                code = performer.code,
                existsImage = performer.existsImage,
                imageUrl = performer.imageUrl,
                name = performer.name,
                presenceStatus = performer.presenceStatus,
                status = performer.status,
                thumbnailImageUrl = performer.thumbnailImageUrl
            )
        }
    }
}
