package jp.careapp.counseling.android.model.network

import com.google.gson.annotations.SerializedName

data class RMPerformerResponse(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("call_restriction")
    val callRestriction: Int?,
    @SerializedName("call_status")
    val callStatus: Int?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("exists_image")
    val existsImage: Boolean?,
    @SerializedName("genres")
    val genres: List<Int>?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("login_plans_datetime")
    val loginPlansDatetime: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("message_notice")
    val messageNotice: String?,
    @SerializedName("message_of_the_day")
    val messageOfTheDay: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("point_per_char")
    val pointPerChar: Int?,
    @SerializedName("point_per_minute")
    val pointPerMinute: Int?,
    @SerializedName("presence_status")
    val presenceStatus: Int?,
    @SerializedName("ranking")
    val ranking: Ranking?,
    @SerializedName("review_average")
    val reviewAverage: Double?,
    @SerializedName("review_score")
    val reviewScore: Int?,
    @SerializedName("review_total_number")
    val reviewTotalNumber: Int?,
    @SerializedName("review_total_score")
    val reviewTotalScore: Int?,
    @SerializedName("sex")
    val sex: Int?,
    @SerializedName("stage")
    val stage: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("thumbnail_image_url")
    val thumbnailImageUrl: String?,
    @SerializedName("chat_status")
    val chatStatus: Int?
): BasePerformerResponse()