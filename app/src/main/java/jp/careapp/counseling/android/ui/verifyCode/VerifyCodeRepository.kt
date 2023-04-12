package jp.careapp.counseling.android.ui.verifyCode

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class VerifyCodeRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun sendVerifyCode(email: String, authCode: String) =
        apiInterface.sendVerifyCode(email, authCode)

    fun saveUserInfo(
        token: String,
        tokenExpire: String,
        password: String,
        memberCode: String
    ) = rxPreferences.saveUserInfo(token, tokenExpire, password, memberCode)

    suspend fun sendVerifyCodeAfterEditEmail(email: String, authCode: String) =
        apiInterface.sendVerifyCodeAfterEditEmail(email, authCode)

    fun saveEmail(email: String) = rxPreferences.saveEmail(email)
}