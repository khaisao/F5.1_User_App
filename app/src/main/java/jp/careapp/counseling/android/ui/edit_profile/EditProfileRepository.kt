package jp.careapp.counseling.android.ui.edit_profile

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class EditProfileRepository @Inject constructor(private val rxPreferences: RxPreferences) {

    fun getMemberNickName() = rxPreferences.getMemberNickName()

    fun getMemberMail() = rxPreferences.getMemberMail()

    fun getMemberAge() = rxPreferences.getMemberAge()
}