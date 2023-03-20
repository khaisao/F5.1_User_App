package jp.careapp.counseling.android.ui.edit_nick_name

import jp.careapp.counseling.android.data.model.ParamsUpdateMember
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class EditNickNameRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun getMemberNickName() = rxPreferences.getNickName()

    suspend fun editMemberName(memberName: String) = apiInterface.updateProfile(
        ParamsUpdateMember(
            memberName,
            rxPreferences.getMemberSex(),
            rxPreferences.getMemberBirth().toString()
        )
    )

    fun saveMemberName(memberName: String) = rxPreferences.setNickName(memberName)
}