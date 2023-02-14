package jp.careapp.counseling.android.data.model.user_profile

import java.io.Serializable

data class UserProfileResponse(
    val age: Int,
    val code: String,
    val exists_image: Boolean,
    val genres: List<Int>,
    val image_url: String,
    val is_favorite: Boolean,
    val login_plans_datetime: String,
    val message: String,
    val name: String,
    val point_per_char: Int,
    val presence_status: Int,
    val ranking: Ranking,
    val review_average: Float,
    val review_score: Int,
    val review_total_number: Int,
    val review_total_score: Int,
    val sex: Any,
    val stage: Int,
    val status: Int,
    val thumbnail_image_url: String
) : Serializable
