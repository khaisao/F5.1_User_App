package jp.slapp.android.android.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InfoRegistrationWithoutEmailRequest(
    var name: String = "",
    var sex: Int = 0,
    var birth: String = "",
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
