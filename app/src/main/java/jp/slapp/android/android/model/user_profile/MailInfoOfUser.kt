package jp.slapp.android.android.model.user_profile

data class MailInfoOfUser(
    val body: String,
    val code: String,
    val exists_atatach_file: Boolean,
    val from_owner_mail: Boolean,
    val image_string: String,
    val open: Boolean,
    val performer: Performer,
    val `return`: Boolean,
    val send_date: String,
    val send_mail: Boolean,
    val subject: String
)
