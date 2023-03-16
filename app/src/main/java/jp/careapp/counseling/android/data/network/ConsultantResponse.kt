package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.data.model.history_chat.Performer
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
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
    @SerializedName("login_plans_datetime") val loginPlansDatetime: String? = "",
    @SerializedName("ranking") val ranking: RankingResponse? = null,
    @SerializedName("recommend_ranking") val recommendRanking: Int = 0,
    @SerializedName("profile_pattern") val profilePattern: Int = 0,
    @SerializedName("profile_images") val profileImages: ProfileImageResponse? = null,
    @SerializedName("presence_status") val presenceStatus: Int = 0,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("login_member_count") val loginMemberCount: Int = 0,
    @SerializedName("peeping_member_count") val peepingMemberCount: Int = 0,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("point_per_minute") val pointPerMinute: Int = 0,
    @SerializedName("call_status") val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status") val chatStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("call_restriction") val callRestriction: Int? = 0, // 1=possible, null=not possible
) : Serializable {
    companion object {
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

        fun isWaiting(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 1 && chatStatus == 0) {
                return true
            } else if (callStatus == 2 && chatStatus == 0) {
                return true
            }
            return false
        }

        fun isLiveStream(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 0 && chatStatus == 1) {
                return true
            } else if (callStatus == 0 && chatStatus == 2) {
                return true
            }
            return false
        }

        fun isPrivateLiveStream(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 0 && chatStatus == 3) {
                return true
            }
            return false
        }

        fun isOnline(callStatus:Int, chatStatus:Int): Boolean {
            if(!isWaiting(callStatus, chatStatus) && !isLiveStream(callStatus,chatStatus) && !isPrivateLiveStream(callStatus, chatStatus)){
                return true
            }
            return false
        }
    }
}
