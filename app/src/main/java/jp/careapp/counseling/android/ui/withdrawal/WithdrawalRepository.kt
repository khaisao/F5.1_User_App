package jp.careapp.counseling.android.ui.withdrawal

import jp.careapp.counseling.android.data.pref.AppPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class WithdrawalRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val appPreferences: AppPreferences
) {

    suspend fun withdrawal(reason: String) = apiInterface.membershipWithdrawal(reason)

    fun clearAppPreferences() = appPreferences.clear()
}