package jp.careapp.counseling.android.ui.edit_mail

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class EditMailRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun getMemberNickMail() = rxPreferences.getEmail()

    suspend fun updateMail(mail: String) = apiInterface.updateEmail(mail)
}