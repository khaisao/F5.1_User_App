package jp.careapp.counseling.android.model.user_profile

data class Performer(
    val code: String,
    val exists_image: Boolean,
    val image_url: Any,
    val name: String,
    val presence_status: Int,
    val status: Int,
    val thumbnail_image_url: Any,
    val callStatus: Int
)
