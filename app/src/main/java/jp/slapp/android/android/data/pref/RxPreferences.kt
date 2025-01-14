package jp.slapp.android.android.data.pref

import jp.slapp.android.android.data.model.CreditItem
import jp.slapp.android.android.data.network.*
import javax.inject.Singleton

@Singleton
interface RxPreferences {
    fun put(key: String, value: String)

    fun get(key: String): String?

    fun clear()

    fun remove(key: String)

    fun getToken(): String?

    fun setUserToken(userToken: String)

    fun logout()

    fun saveEmail(email: String)

    fun getEmail(): String?

    fun saveUserInfo(token: String, tokenExpire: String, password: String, memberCode: String)

    fun updateUserInformation(token: String?, tokenExpire: String?, memberCode: String?)

    fun getTokenExpire(): String?

    fun getPassword(): String?

    fun getMemberCode(): String?

    fun getSignupStatus(): Int?

    fun saveNewLastViewDateTime(dateTime: String)

    fun getNewLastViewDateTime(): String?

    fun saveMemberInfo(memberResponse: MemberResponse)

    fun getPoint(): Int

    fun setPoint(point: Int)

    fun getTimeBuy(): Long

    fun saveDeviceToken(deviceToken: String)

    fun getDeviceToken(): String?

    fun saveNumberUnreadMessage(number: Int)

    fun getNumberUnreadMessage(): Int

    fun saveDeepLink(deepLink: String)

    fun getDeepLink(): String?

    fun isFirstShowGuideUser(): Boolean

    fun saveFirstShowGuideUser(isFirstUse: Boolean)

    fun getTroubleInfo(): TroubleSheetResponse?

    fun saveTroubleSheet(troubleSheetResponse: TroubleSheetResponse)

    fun saveListCategory(listCategory: List<CategoryResponse>)

    fun getListCategory(): List<CategoryResponse>?

    fun setFirstRegister(isTheFirst: Boolean)

    fun getFirstRegister(): Boolean

    fun getFirstBuyCredit(): Boolean

    fun saveFirstReview(isTheFirst: Boolean, memberCode: String?, performerCode: String)

    fun isFirstReview(memberCode: String?, performerCode: String): Boolean

    fun getDesiredResponse(): Int

    fun setDesiredResponse(status: Int)

    fun getGenre(): Int

    fun setGenre(id: Int)

    fun getLastBuyLog(): LastBuyLog?

    fun saveCreditPrices(data: List<CreditItem>)

    fun getCreditPrices(): List<CreditItem>

    fun setCallToken(token: String)

    fun getCallToken(): String

    fun saveConfigCall(config: ConfigCallResponse)

    fun getConfigCall(): ConfigCallResponse

    fun isReviewMode(): Boolean

    fun saveUserInformationRM(
        memberCode: String?,
        email: String?,
        password: String?,
        token: String?,
        tokenExpire: String?
    )

    fun setNickName(nickName: String)

    fun getNickName(): String?

    fun setContent(content: String)

    fun getContent(): String?

    fun setPushMail(pushMail: Int)

    fun getPushMail(): Int

    fun setAppMode(appMode: Int)

    fun getAppMode(): Int

    fun saveMemberInfoEditProfile(
        name: String,
        mail: String,
        age: Int,
        birth: String,
        sex: Int,
        point: Int,
        statusNotification: Int,
        statusNoticeMail: Int,
        statusNewsLetterMail: Int
    )

    fun getMemberAge(): Int

    fun getMemberBirth(): String?

    fun getMemberSex(): Int

    fun getMemberPoint(): Int

    fun saveSettingNotificationNM(statusNotification: Int)

    fun getSettingNotificationNM(): Int

    fun getSettingReceiveNoticeMail(): Int

    fun getSettingReceiveNewsLetterNoticeMail(): Int

    fun saveSettingReceiveNoticeMail(status: Int)

    fun saveSettingReceiveNewsLetterNoticeMail(status: Int)

    fun saveMemberAge(age: Int)

    fun switchMode()

    fun saveMemberBirth(birth: String)
}
