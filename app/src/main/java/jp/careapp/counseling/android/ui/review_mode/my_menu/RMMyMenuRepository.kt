package jp.careapp.counseling.android.ui.review_mode.my_menu

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import javax.inject.Inject

class RMMyMenuRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun withdrawal(reason: String) = rmApiInterface.withdrawal(reason)

    fun getPoint() = rxPreferences.getPoint()

    fun getNickName() = rxPreferences.getNickName()
}