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



        fun isWaiting(consultantResponse: ConsultantResponse): Boolean {
            var isWaiting = false
            if (consultantResponse.callStatus == 1 && consultantResponse.chatStatus == 0) {
                isWaiting = true
            } else if (consultantResponse.callStatus == 2 && consultantResponse.chatStatus == 0) {
                isWaiting = true
            }
            return true
        }

        fun isLiveStream(consultantResponse: ConsultantResponse): Boolean {
            var isLiveStream = false
            if (consultantResponse.callStatus == 0 && consultantResponse.chatStatus == 1) {
                isLiveStream = true
            } else if (consultantResponse.callStatus == 0 && consultantResponse.chatStatus == 2) {
                isLiveStream = true
            }
            return isLiveStream
        }

        fun isPrivateLiveStream(consultantResponse: ConsultantResponse): Boolean {
            var isPrivateLiveStream = false
            if (consultantResponse.callStatus == 0 && consultantResponse.chatStatus == 3) {
                isPrivateLiveStream = true
            }
            return isPrivateLiveStream
        }

        fun isOnline(consultantResponse: ConsultantResponse): Boolean {
            if(!isWaiting(consultantResponse) && !isLiveStream(consultantResponse) && !isPrivateLiveStream(consultantResponse)){
                return true
            }
            return false
        }
    }
}
