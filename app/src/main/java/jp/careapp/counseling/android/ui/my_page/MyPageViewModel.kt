package jp.careapp.counseling.android.ui.my_page

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TypeField
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.BLOCK_LIST
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.BUY_POINTS
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.CONTACT_US
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.FAQ
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.PRIVACY_POLICY
import jp.careapp.counseling.android.ui.my_page.NMMenuItemKey.PROFILE
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

    private val _memberAge = MutableLiveData<Int>()
    val memberAge: LiveData<Int>
        get() = _memberAge

    private val _memberPoint = MutableLiveData<Int>()
    val memberPoint: LiveData<Int>
        get() = _memberPoint

    private val _dataLiveData = MutableLiveData<ArrayList<NMMenuItem>>()
    val dataLiveData: LiveData<ArrayList<NMMenuItem>>
        get() = _dataLiveData

    val mActionState = SingleLiveEvent<MyPageActionState>()

    init {
        getDataMyPageMenu()
        getMemberInfo()
    }

    private fun getDataMyPageMenu() {
        val data = arrayListOf<NMMenuItem>()
        data.add(
            NMMenuItem.NMMenuItemField(
                BUY_POINTS,
                R.drawable.ic_cell_buy_points,
                "ポイント購入",
                TypeField.TOP
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                PROFILE,
                R.drawable.ic_cell_profile,
                "プロフィール",
                TypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                BLOCK_LIST,
                R.drawable.ic_cell_block,
                "ブロック一覧",
                TypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                SETTING_PUSH,
                R.drawable.ic_cell_notifications,
                "通知設定",
                TypeField.BOTTOM
            )
        )
        data.add(NMMenuItem.NMMenuItemSpace)
        data.add(
            NMMenuItem.NMMenuItemField(
                USE_POINTS_GUIDE,
                R.drawable.ic_cell_document,
                "料金説明",
                TypeField.TOP
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                TERMS_OF_SERVICE,
                R.drawable.ic_cell_document,
                "利用規約",
                TypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                PRIVACY_POLICY,
                R.drawable.ic_cell_document,
                "プライバシーポリシー",
                TypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                FAQ,
                R.drawable.ic_cell_questions,
                "よくある質問",
                TypeField.CENTER
            )
        )
        data.add(
            NMMenuItem.NMMenuItemField(
                CONTACT_US,
                R.drawable.ic_cell_mail,
                "お問い合わせ",
                TypeField.BOTTOM
            )
        )
        _dataLiveData.value = data
    }

    private fun getMemberInfo() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.getMemberInfo()
                    if (response.errors.isEmpty()) {
                        response.dataResponse.let {
                            withContext(Dispatchers.Main) {
                                _memberName.value = it.name
                                _memberAge.value = it.age
                            }
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

    fun onClickItemMenu(position: Int) {
        when (position) {
            TERMS_OF_SERVICE -> mActionState.value = MyPageActionState.NavigateToTermOfService
            PRIVACY_POLICY -> mActionState.value = MyPageActionState.NavigateToPrivacyPolicy
        }
    }

//    private val _newMessage = MutableLiveData<SocketActionSend>()
//    val newMessage: LiveData<SocketActionSend> get() = _newMessage
//    private val _refreshMember = MutableLiveData<Unit>()
//    private var prevHasTroubleState: Boolean? = null
//    fun forceRefresh() {
//        _refreshMember.value = Unit
//        prevHasTroubleState = null
//    }
//
//    private val _memberResult: LiveData<Result<ApiObjectResponse<MemberResponse>>> =
//        _refreshMember.switchMap {
//            liveData {
//                emit(Result.Loading)
//                emit(getMemberResult())
//            }
//        }
//    val memberLoading: LiveData<Boolean> = _memberResult.map {
//        it == Result.Loading
//    }
//
//    val uiMember: LiveData<MemberResponse> = _memberResult.map {
//        (it as? Result.Success)?.data?.dataResponse ?: MemberResponse()
//    }
//
//    val needUpdateTroubleSheetBadge: LiveData<Boolean> = _memberResult.map {
//        if (it !is Result.Success) {
//            false
//        } else {
//            with(it.data?.dataResponse?.isHaveTroubleSheet) {
//                if (this != prevHasTroubleState) {
//                    prevHasTroubleState = this
//                    true
//                } else {
//                    false
//                }
//            }
//        }
//    }
//
//    private suspend fun getMemberResult(): Result<ApiObjectResponse<MemberResponse>> {
//        return try {
//            withContext(Dispatchers.IO) {
//                apiService.getMember().let {
//                    Result.Success(it)
//                }
//            }
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }
//
//    private val _countNotification: LiveData<Result<ApiObjectResponse<NotificationResponse>>> =
//        uiMember.switchMap { data ->
//            liveData {
//                if (!data.newsLastViewDateTime.isNullOrEmpty()) {
//                    emit(Result.Loading)
//                    emit(getCountNotification(data.newsLastViewDateTime))
//                } else {
//                    emit(Result.Loading)
//                    emit(
//                        getCountNotification(
//                            LocalDateTime.now()
//                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//                                .toString()
//                        )
//                    )
//                }
//            }
//        }
//    val uiNotification: LiveData<NotificationResponse> = _countNotification.map {
//        (it as? Result.Success)?.data?.dataResponse ?: NotificationResponse()
//    }
//
//    private suspend fun getCountNotification(startDate: String): Result<ApiObjectResponse<NotificationResponse>> {
//        return try {
//            withContext(Dispatchers.IO) {
//                apiService.getCountNotification(startDate = startDate).let {
//                    Result.Success(it)
//                }
//            }
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }
//
//    private val _navigateToEditProfileFragmentAction = MutableLiveData<Event<MemberResponse?>>()
//    val navigateToEditProfileFragmentAction: LiveData<Event<MemberResponse?>> =
//        _navigateToEditProfileFragmentAction
//    private val _destination = MutableLiveData<Event<Destination>>()
//    val destination: LiveData<Event<Destination>> = _destination
//    override fun onclickItem(item: MyPageItem) {
//        viewModelScope.launch {
//            _destination.value = Event(item.destination)
//        }
//    }
//
//    fun editProfile(memberResponse: MemberResponse?) {
//        viewModelScope.launch {
//            _navigateToEditProfileFragmentAction.value = Event(memberResponse)
//        }
//    }
//
//    private val _memberError = MediatorLiveData<Event<String>>().apply {
//        addSource(_memberResult) { rs ->
//            if (rs is Result.Error)
//                value = Event(content = rs.throwable.message ?: "")
//        }
//        addSource(_countNotification) { rs ->
//            if (rs is Result.Error)
//                value = Event(content = rs.throwable.message ?: "")
//        }
//    }
//    val memberMessage: LiveData<Event<String>> = _memberError
}

sealed class MyPageActionState {
    object NavigateToTermOfService : MyPageActionState()
    object NavigateToPrivacyPolicy : MyPageActionState()
}
