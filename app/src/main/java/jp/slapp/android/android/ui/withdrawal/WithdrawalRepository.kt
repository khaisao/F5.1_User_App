package jp.slapp.android.android.ui.withdrawal

import jp.slapp.android.android.data.pref.AppPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class WithdrawalRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val appPreferences: AppPreferences
) {

    suspend fun withdrawal(reason: String) = apiInterface.membershipWithdrawal(reason)

    fun clearAppPreferences() = appPreferences.clear()
}