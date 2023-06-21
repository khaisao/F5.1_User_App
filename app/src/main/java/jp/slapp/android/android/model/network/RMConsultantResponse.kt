package jp.slapp.android.android.model.network

import com.google.gson.annotations.SerializedName
import jp.slapp.android.android.data.model.message.Performer
import jp.slapp.android.android.data.network.RankingResponse
import java.io.Serializable

data class RMConsultantResponse(
    @SerializedName("code") val code: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("sex") val sex: Int? = 3,
    @SerializedName("age") val age: Int? = 0,
    @SerializedName("exists_image") val existsImage: Boolean? = false,
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("message_of_the_day") val messageOfTheDay: String? = "",
    @SerializedName("genres") val genres: List<Int?> = listOf(),
    @SerializedName("point_per_minute") val pointPerMinute: Int? = 0,
    @SerializedName("profile_pattern") val profilePattern: Int? = 0,
    @SerializedName("call_status") val callStatus: Int? = 0,
    @SerializedName("chat_status") val chatStatus: Int? = 0,
    @SerializedName("stage") val stage: Int? = 0,
    @SerializedName("review_average") val reviewAverage: Double? = 0.0,
    @SerializedName("review_score") val reviewScore: Int? = 0,
    @SerializedName("review_total_number") val reviewTotalNumber: Int? = 0,
    @SerializedName("pay_open_user_count") val payOpenUserCount: Int? = 0,
    @SerializedName("review_total_score") val reviewTotalScore: Int? = 0,
    @SerializedName("point_per_char") val pointPerChar: Int? = 0,
    @SerializedName("login_plans_datetime") val loginPlansDatetime: String? = "",
    @SerializedName("ranking") val ranking: RankingResponse? = null,
    @SerializedName("presence_status") val presenceStatus: Int? = 0,
    @SerializedName("status") val status: Int? = 0,
    @SerializedName("is_favorite") var isFavorite: Boolean? = false,
    @SerializedName("profile_images") var imageProfile: ImageProfile? = null,
    @SerializedName("message_notice") var messageNotice: String? = ""
) : Serializable {
    companion object {
        fun from(performer: Performer): RMConsultantResponse {
            return RMConsultantResponse(
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

data class ImageProfile(
    @SerializedName("square") val square: Image? = null,
    @SerializedName("rectangle") val rectangle: Image? = null,
) : Serializable

data class Image(
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String? = "",
) : Serializable
