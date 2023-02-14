package jp.careapp.counseling.android.ui.mypage

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.model.network.NotificationResponse
import jp.careapp.counseling.android.data.model.MyPageItem
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyPageViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
) : BaseViewModel(), EventAction {

    private val _newMessage = MutableLiveData<SocketActionSend>()
    val newMessage: LiveData<SocketActionSend> get() = _newMessage
    private val _refreshMember = MutableLiveData<Unit>()
    private var prevHasTroubleState: Boolean? = null
    fun forceRefresh() {
        _refreshMember.value = Unit
        prevHasTroubleState = null
    }

    private val _memberResult: LiveData<Result<ApiObjectResponse<MemberResponse>>> =
        _refreshMember.switchMap {
            liveData {
                emit(Result.Loading)
                emit(getMemberResult())
            }
        }
    val memberLoading: LiveData<Boolean> = _memberResult.map {
        it == Result.Loading
    }

    val uiMember: LiveData<MemberResponse> = _memberResult.map {
        (it as? Result.Success)?.data?.dataResponse ?: MemberResponse()
    }

    val needUpdateTroubleSheetBadge: LiveData<Boolean> = _memberResult.map {
        if (it !is Result.Success) {
            false
        } else {
            with(it.data?.dataResponse?.isHaveTroubleSheet) {
                if (this != prevHasTroubleState) {
                    prevHasTroubleState = this
                    true
                } else {
                    false
                }
            }
        }
    }

    private suspend fun getMemberResult(): Result<ApiObjectResponse<MemberResponse>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getMember().let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _countNotification: LiveData<Result<ApiObjectResponse<NotificationResponse>>> =
        uiMember.switchMap { data ->
            liveData {
                if (!data.newsLastViewDateTime.isNullOrEmpty()) {
                    emit(Result.Loading)
                    emit(getCountNotification(data.newsLastViewDateTime))
                } else {
                    emit(Result.Loading)
                    emit(getCountNotification(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()))
                }
            }
        }
    val uiNotification: LiveData<NotificationResponse> = _countNotification.map {
        (it as? Result.Success)?.data?.dataResponse ?: NotificationResponse()
    }

    private suspend fun getCountNotification(startDate: String): Result<ApiObjectResponse<NotificationResponse>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getCountNotification(startDate = startDate).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _navigateToEditProfileFragmentAction = MutableLiveData<Event<MemberResponse?>>()
    val navigateToEditProfileFragmentAction: LiveData<Event<MemberResponse?>> =
        _navigateToEditProfileFragmentAction
    private val _destination = MutableLiveData<Event<Destination>>()
    val destination: LiveData<Event<Destination>> = _destination
    override fun onclickItem(item: MyPageItem) {
        viewModelScope.launch {
            _destination.value = Event(item.destination)
        }
    }

    fun editProfile(memberResponse: MemberResponse?) {
        viewModelScope.launch {
            _navigateToEditProfileFragmentAction.value = Event(memberResponse)
        }
    }

    private val _memberError = MediatorLiveData<Event<String>>().apply {
        addSource(_memberResult) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_countNotification) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val memberMessage: LiveData<Event<String>> = _memberError
}

enum class Destination {
    SHEET,
    LAB,
    BUY_POINT,
    NEWS,
    ALERT,
    HELP,
    SETTING,
    CONTACT
}
