package jp.careapp.counseling.android.ui.my_page

import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class MyPageRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    suspend fun getMemberInfo() = apiInterface.getMember()

    fun saveMemberInfoEditProfile(
        nickName: String,
        mail: String,
        age: Int,
        birth: String,
        sex: Int,
        point: Int,
        statusNotification: Int,
        receiveNoticeMail: Int,
        receiveNewsLetterMail: Int
    ) = rxPreferences.saveMemberInfoEditProfile(
        nickName,
        mail,
        age,
        birth,
        sex,
        point,
        statusNotification,
        receiveNoticeMail,
        receiveNewsLetterMail
    )

    fun getMemberNickName() = rxPreferences.getNickName()

    fun getMemberAge() = rxPreferences.getMemberAge()

    fun getMemberPoint() = rxPreferences.getMemberPoint()

    fun getMemberBirth() = rxPreferences.getMemberBirth()
}