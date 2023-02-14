package jp.careapp.counseling.android.ui.notification

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.UpdateNotificationParams
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
    @Assisted
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _saveStateSwitch = MutableLiveData<SaveStateSwitch>()
    val saveStateSwitch: LiveData<SaveStateSwitch> = _saveStateSwitch
    fun setSaveStateSwitchFromMemberResponse(memberResponse: MemberResponse) {
        memberResponse.let {
            _saveStateSwitch.value = SaveStateSwitch(
                it.receiveNewsletterMail,
                it.pushNewsletter,
                it.pushMail,
                it.pushOnline,
                it.pushCounseling,
                it.receiveNoticeMail
            )
        }
    }

    private val _openDirect = MutableLiveData<Boolean>()
    val openDirect: LiveData<Boolean> = _openDirect

    init {
        savedStateHandle.get<MemberResponse>("member")?.let {
            _saveStateSwitch.value = SaveStateSwitch(
                it.receiveNewsletterMail,
                it.pushNewsletter,
                it.pushMail,
                it.pushOnline,
                it.pushCounseling,
                it.receiveNoticeMail
            )
        }
        savedStateHandle.get<Boolean>(Define.Intent.OPEN_DIRECT)?.let {
            _openDirect.value = it
        }
    }

    private val _setParamsUpdate = MutableLiveData<UpdateNotificationParams>()
    fun setParamsUpdate(updateNotificationParams: UpdateNotificationParams) {
        _setParamsUpdate.value = updateNotificationParams
    }

    private val _updateNotificationResult: LiveData<Result<ApiObjectResponse<Any>>> =
        _setParamsUpdate.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(updateNotification(data))
            }
        }
    val updateNotificationLoading: LiveData<Boolean> = _updateNotificationResult.map {
        it == Result.Loading
    }

    //    val updateNotificationResult:LiveData<Result<ApiObjectResponse<Any>>> = _updateNotificationResult
    private suspend fun updateNotification(updateNotificationParams: UpdateNotificationParams): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.updateNotification(updateNotificationParams).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_updateNotificationResult) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error
}
