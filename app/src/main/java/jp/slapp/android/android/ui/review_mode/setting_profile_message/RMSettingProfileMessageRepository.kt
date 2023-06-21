package jp.slapp.android.android.ui.review_mode.setting_profile_message

import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.RMApiInterface
import javax.inject.Inject

class RMSettingProfileMessageRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun saveProfileMessage(profileMessage: String) = rmApiInterface.saveProfileMessage(profileMessage)

    fun getProfileMessagePreferences() = rxPreferences.getContent()

    fun saveProfileMessagePreferences(profileMessage: String) = rxPreferences.setContent(profileMessage)
}