package jp.slapp.android.android.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InforRegistrationRequest(
    var token: String = "",
    var name: String = "",
    var sex: Int = 0,
    var birth: String = "",
    @SerializedName("receive_newsletter_mail")
    var receiveNewsLetterEmail: Int = 1,
    @SerializedName("receive_notice_mail")
    var receiveNoticeMail: Int = 0,
    @SerializedName("push_newsletter")
    var pushNewsletter: Int = 0,
    @SerializedName("push_mail")
    var pushMail: Int = 0,
    @SerializedName("push_online")
    var pushOnline: Int = 0,
    @SerializedName("push_counseling")
    var pushCounseling: Int = 0,
    @SerializedName("os")
    var os: String = "Android",
    @SerializedName("android_id")
    var androidId: String = "",
) : Serializable
