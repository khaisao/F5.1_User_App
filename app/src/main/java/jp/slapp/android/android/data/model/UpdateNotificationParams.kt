package jp.slapp.android.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateNotificationParams(
    @SerializedName("receive_newsletter_mail") val receiveNewsletterMail: Int = 1,
    @SerializedName("push_newsletter") val pushNewsletter: Int = 1,
    @SerializedName("push_mail") val pushMail: Int = 1,
    @SerializedName("push_online") val pushOnline: Int = 1,
    @SerializedName("push_counseling") val pushCounseling: Int = 1,
    @SerializedName("receive_notice_mail") val receiverNoticeMail: Int = 1
) : Parcelable
