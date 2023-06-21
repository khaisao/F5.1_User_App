package jp.slapp.android.android.ui.verificationCodeHelp

import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class VerifyCodeHelpRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun resendOtp(mail: String) = apiInterface.sendEmail(mail)
}