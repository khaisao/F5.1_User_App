package jp.careapp.counseling.android.ui.verificationCodeHelp

import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class VerifyCodeHelpRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun resendOtp(mail: String) = apiInterface.sendEmail(mail)
}