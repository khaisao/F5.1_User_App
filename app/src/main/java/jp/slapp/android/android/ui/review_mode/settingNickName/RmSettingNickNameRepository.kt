package jp.slapp.android.android.ui.review_mode.settingNickName

import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.RMApiInterface
import javax.inject.Inject

class RmSettingNickNameRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun updateNickName(nickName: String) = rmApiInterface.updateNickName(nickName)

    fun getNickNamePreferences() = rxPreferences.getNickName()

    fun saveNickNamePreferences(nickName: String) = rxPreferences.setNickName(nickName)
}