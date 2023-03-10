package jp.careapp.counseling.android.ui.edit_profile

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class EditProfileRepository @Inject constructor(private val rxPreferences: RxPreferences) {

    fun getMemberNickName() = rxPreferences.getNickName()

    fun getMemberMail() = rxPreferences.getEmail()

    fun getMemberAge() = rxPreferences.getMemberAge()
}