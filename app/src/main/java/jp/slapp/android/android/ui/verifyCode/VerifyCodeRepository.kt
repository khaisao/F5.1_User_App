package jp.slapp.android.android.ui.verifyCode

import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class VerifyCodeRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun sendVerifyCode(email: String, authCode: String) =
        apiInterface.sendVerifyCode(email, authCode)

    suspend fun getMemberInfo() = apiInterface.getMember()

    fun saveUserInfo(
        token: String,
        tokenExpire: String,
        password: String,
        memberCode: String
    ) = rxPreferences.saveUserInfo(token, tokenExpire, password, memberCode)

    fun saveMemberInfo(
        memberResponse: MemberResponse
    ) = rxPreferences.saveMemberInfo(memberResponse)

    suspend fun sendVerifyCodeAfterEditEmail(email: String, authCode: String) =
        apiInterface.sendVerifyCodeAfterEditEmail(email, authCode)

    fun saveEmail(email: String) = rxPreferences.saveEmail(email)
}