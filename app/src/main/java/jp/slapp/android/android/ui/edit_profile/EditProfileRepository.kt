package jp.slapp.android.android.ui.edit_profile

import jp.slapp.android.android.data.model.ParamsUpdateMember
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun getMemberNickName() = rxPreferences.getNickName().toString()

    fun getMemberMail() = rxPreferences.getEmail().toString()

    fun getMemberBirth() = rxPreferences.getMemberBirth()

    fun getMemberAge() = rxPreferences.getMemberAge().toString()

    fun saveMemberBirth(birth: String) = rxPreferences.saveMemberBirth(birth)

    fun saveMemberAge(age: Int) = rxPreferences.saveMemberAge(age)

    suspend fun editMemberBirth(memberBirth: String) = apiInterface.updateProfile(
        ParamsUpdateMember(
            getMemberNickName(),
            rxPreferences.getMemberSex(),
            memberBirth
        )
    )
}