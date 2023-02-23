package jp.careapp.counseling.android.ui.review_mode.enterName

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import javax.inject.Inject

class RMEnterNameRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) {
    suspend fun setNickNameRM(nickName: String) = rmApiInterface.setNickNameRM(nickName)

    fun saveUserInfoRM(
        memberCode: String?,
        email: String?,
        password: String?,
        token: String?,
        tokenExpire: String?
    ) = rxPreferences.saveUserInformationRM(memberCode, email, password, token, tokenExpire)
}