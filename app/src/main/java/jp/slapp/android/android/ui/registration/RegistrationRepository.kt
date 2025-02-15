package jp.slapp.android.android.ui.registration

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import jp.slapp.android.R
import jp.slapp.android.android.data.model.InfoRegistrationWithEmailRequest
import jp.slapp.android.android.data.model.InfoRegistrationWithoutEmailRequest
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class RegistrationRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val application: Application
) {

    suspend fun getMemberInfo() = apiInterface.getMember()

    suspend fun registerWithEmail(userName: String, receiveMail: Int, token: String) = apiInterface.registerWithEmail(
        InfoRegistrationWithEmailRequest(
            token = token,
            name = userName,
            sex = 1,
            birth = application.getString(R.string.birth_default),
            receiveNewsLetterEmail = receiveMail,
            receiveNoticeMail = receiveMail,
            pushNewsletter = 1,
            pushMail = 1,
            pushOnline = 1,
            pushCounseling = 1,
            androidId = getAndroidId()
        )
    )

    suspend fun registerWithoutEmail(userName: String) = apiInterface.registerWithoutEmail(
        InfoRegistrationWithoutEmailRequest(
            name = userName,
            sex = 1,
            birth = application.getString(R.string.birth_default),
            pushNewsletter = 1,
            pushMail = 0,
            pushOnline = 1,
            pushCounseling = 1,
            androidId = getAndroidId()
        )
    )

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            application.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun saveUserInfo(
        token: String,
        tokenExpire: String,
        password: String,
        memberCode: String
    ) = rxPreferences.saveUserInfo(token, tokenExpire, password, memberCode)

    fun setFirstRegister() = rxPreferences.setFirstRegister(true)

    fun saveMemberName(userName: String) = rxPreferences.setNickName(userName)
}