package jp.slapp.android.android.ui.blocked

import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class BlockedRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getMemberBlocked() = apiInterface.getMemberBlocked()

    suspend fun deleteBlocked(memberCode: String) = apiInterface.deleteBlocked(memberCode)
}