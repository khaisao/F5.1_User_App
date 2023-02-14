package jp.careapp.counseling.android.model.user_profile

data class ReviewUserProfile(
    val id: Int,
    val member: Member,
    val performer_code: String,
    val point: Int,
    val review: String,
    val status: Int
)
