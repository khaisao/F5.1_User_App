package jp.careapp.counseling.android.ui.splash

import jp.careapp.counseling.android.data.pref.RxPreferences
import javax.inject.Inject

class SplashRepository @Inject constructor(private val rxPreferences: RxPreferences) {
    fun getUserTokenExpire() = rxPreferences.getTokenExpire()
}