package jp.slapp.android.android.ui.edit_mail

import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class EditMailRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun getMemberNickMail() = rxPreferences.getEmail()

    suspend fun updateMail(mail: String) = apiInterface.updateEmail(mail)
}