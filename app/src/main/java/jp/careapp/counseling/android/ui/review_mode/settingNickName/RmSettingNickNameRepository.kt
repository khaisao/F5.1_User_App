package jp.careapp.counseling.android.ui.review_mode.settingNickName

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import javax.inject.Inject

class RmSettingNickNameRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun updateNickName(nickName: String) = rmApiInterface.updateNickName(nickName)

    fun getNickNamePreferences() = rxPreferences.getNickName()

    fun saveNickNamePreferences(nickName: String) = rxPreferences.setNickName(nickName)
}