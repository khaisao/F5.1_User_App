package jp.careapp.counseling.android.ui.my_page

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.NMTypeField
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.BLOCK_LIST
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.BUY_POINTS
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.CONTACT_US
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.EDIT_PROFILE
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.FAQ
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.PRIVACY_POLICY
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.SETTING_PUSH
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.TERMS_OF_SERVICE
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.USE_POINTS_GUIDE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val mRepository: MyPageRepository
) : BaseViewModel() {

    private val _memberName = MutableLiveData<String>()
    val memberName: LiveData<String>
        get() = _memberName

    private val _memberAge = MutableLiveData<String>()
    val memberAge: LiveData<String>
        get() = _memberAge

    private val _memberPoint = MutableLiveData<Int>()
    val memberPoint: LiveData<Int>
        get() = _memberPoint

    val data = arrayListOf<NMMenuItem>()

    private val _dataLiveData = MutableLiveData<ArrayList<NMMenuItem>>()
    val dataLiveData: LiveData<ArrayList<NMMenuItem>>
        get() = _dataLiveData

    val mActionState = SingleLiveEvent<MyPageActionState>()

    init {
        getDataMyPageMenu()
    }

    private fun getDataMyPageMenu() {
        data.add(
            NMMenuItem.NMMenuItemField(
                BUY_POINTS, R.drawable.ic_cell_buy_points, "ポイント購入", NMTypeField.TOP
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                EDIT_PROFILE, R.drawable.ic_cell_profile, "プロフィール", NMTypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                BLOCK_LIST, R.drawable.ic_cell_block, "ブロック一覧", NMTypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                SETTING_PUSH, R.drawable.ic_cell_notifications, "通知設定", NMTypeField.BOTTOM
            )
        )
        data.add(NMMenuItem.NMMenuItemSpace)
        data.add(
            NMMenuItem.NMMenuItemField(
                USE_POINTS_GUIDE, R.drawable.ic_cell_document, "料金説明", NMTypeField.TOP
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                TERMS_OF_SERVICE, R.drawable.ic_cell_document, "利用規約", NMTypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                PRIVACY_POLICY, R.drawable.ic_cell_document, "プライバシーポリシー", NMTypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                FAQ, R.drawable.ic_cell_questions, "よくある質問", NMTypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                CONTACT_US, R.drawable.ic_cell_mail, "お問い合わせ", NMTypeField.BOTTOM
            )
        )
        _dataLiveData.value = data
    }

    fun getMemberInfo() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.getMemberInfo()
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        mRepository.saveMemberInfoEditProfile(
                            dataResponse.name,
                            dataResponse.mail,
                            dataResponse.age,
                            dataResponse.birth,
                            dataResponse.sex,
                            dataResponse.point,
                            dataResponse.pushMail,
                            dataResponse.receiveNoticeMail,
                            dataResponse.receiveNewsletterMail
                        )
                        withContext(Dispatchers.Main) {
                            _memberName.value = mRepository.getMemberNickName()
                            _memberPoint.value = mRepository.getMemberPoint()
                            showMemberAge(mRepository.getMemberBirth().toString())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    private fun showMemberAge(memberBirth: String) {
        if (memberBirth == "1900-01-01") {
            _memberAge.value = "未設定"
        } else {
            _memberAge.value = "${mRepository.getMemberAge()}歳"
        }
    }

    fun onClickItemMenu(position: Int) {
        val menuItem = data[position] as NMMenuItem.NMMenuItemField

        when (menuItem.key) {
            BUY_POINTS -> mActionState.value = MyPageActionState.NavigateToBuyPoints
            EDIT_PROFILE -> mActionState.value = MyPageActionState.NavigateToEditProfile
            BLOCK_LIST -> mActionState.value = MyPageActionState.NavigateToBlocked
            SETTING_PUSH -> mActionState.value = MyPageActionState.NavigateToSettingNotification
            USE_POINTS_GUIDE -> mActionState.value = MyPageActionState.NavigateToUsePointsGuide
            TERMS_OF_SERVICE -> mActionState.value = MyPageActionState.NavigateToTermOfService
            PRIVACY_POLICY -> mActionState.value = MyPageActionState.NavigateToPrivacyPolicy
            FAQ -> mActionState.value = MyPageActionState.NavigateToFAQ
            CONTACT_US -> mActionState.value = MyPageActionState.NavigateToContactUs
        }
    }
}

sealed class MyPageActionState {

    object NavigateToBuyPoints : MyPageActionState()

    object NavigateToEditProfile : MyPageActionState()
    object NavigateToBlocked : MyPageActionState()
    object NavigateToSettingNotification : MyPageActionState()
    object NavigateToUsePointsGuide : MyPageActionState()
    object NavigateToTermOfService : MyPageActionState()
    object NavigateToPrivacyPolicy : MyPageActionState()
    object NavigateToFAQ : MyPageActionState()
    object NavigateToContactUs : MyPageActionState()
}
