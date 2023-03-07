package jp.careapp.counseling.android.ui.my_page

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class MyPageRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun getMemberInfo() = apiInterface.getMember()

    fun saveMemberInfoEditProfile(nickName: String, mail: String, age: Int, birth: String) =
        rxPreferences.saveMemberInfoEditProfile(nickName, mail, age, birth)

    fun getMemberNickName() = rxPreferences.getMemberNickName()

    fun getMemberAge() = rxPreferences.getMemberAge()
}