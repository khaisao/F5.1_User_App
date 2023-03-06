package jp.careapp.counseling.android.ui.my_page

import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class MyPageRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getMemberInfo() = apiInterface.getMember()
}