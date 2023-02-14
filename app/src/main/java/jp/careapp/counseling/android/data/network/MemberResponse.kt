package jp.careapp.counseling.android.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberResponse(
    @SerializedName("code") val code: String = "",
    @SerializedName("mail") val mail: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("sex") val sex: Int = 1,
    @SerializedName("birth") val birth: String = "",
    @SerializedName("age") val age: Int = 1,
    @SerializedName("buy_times") val buyTime: Long = 1,
    @SerializedName("trouble_sheet") val troubleSheetResponse: TroubleSheetResponse = TroubleSheetResponse(),
    @SerializedName("receive_newsletter_mail") var receiveNewsletterMail: Int = 1,
    @SerializedName("receive_notice_mail") var receiveNoticeMail: Int = 0,
    @SerializedName("push_newsletter") var pushNewsletter: Int = 1,
    @SerializedName("push_mail") var pushMail: Int = 1,
    @SerializedName("push_online") var pushOnline: Int = 1,
    @SerializedName("push_counseling") val pushCounseling: Int = 1,
    @SerializedName("news_last_view_datetime") val newsLastViewDateTime: String = "",
    @SerializedName("credit_free_point_status") val creditFreePointStatus: String = "",
    @SerializedName("display_mode") val disPlay: Int = 1,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("point") val point: Int = 0,
    @SerializedName("first_buy_credit") val firstBuyCredit: Boolean = true,
    @SerializedName("counselee_name") val counseleeName: String = "",
    @SerializedName("signup_status") val signupStatus: Int? = null,
    @SerializedName("sdk_user") val sdkUser: Int? = null,
    @SerializedName("last_buylog") val lastBuyLog: LastBuyLog? = null,
) : Parcelable {
    val isHaveTroubleSheet: Boolean
        get() = troubleSheetResponse.content.isNotEmpty()
}
