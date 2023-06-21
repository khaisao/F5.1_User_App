package jp.slapp.android.android.ui.splash

import jp.slapp.android.android.data.pref.RxPreferences
import javax.inject.Inject

class SplashRepository @Inject constructor(private val rxPreferences: RxPreferences) {
    fun getUserTokenExpire() = rxPreferences.getTokenExpire()
}