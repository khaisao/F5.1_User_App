package jp.careapp.counseling.android.ui.withdrawal

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.dummyCategoryData
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class WithdrawalViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
    private val rxPreferences: RxPreferences

) : BaseViewModel() {
    private val _paramsMembershipWithdrawal = MutableLiveData<String>()
    fun setParamsWithdrawal(reason: String) {
        _paramsMembershipWithdrawal.value = reason
    }

    private val _membershipWithdrawal: LiveData<Result<ApiObjectResponse<Any>>> =
        _paramsMembershipWithdrawal.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(membershipWithdrawal(data))
            }
        }
    val loading: LiveData<Boolean> = _membershipWithdrawal.map {
        it == Result.Loading
    }

    suspend fun membershipWithdrawal(reason: String): Result<ApiObjectResponse<Any>> {
        return try {
            apiService.membershipWithdrawal(reason).let {
                Result.Success(it)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_membershipWithdrawal) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error

    fun saveListCategory() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                val response = apiService.getListCategory()
                response.let {
                    if (it.errors.isEmpty()) {
                        rxPreferences.saveListCategory(it.dataResponse)
                    }
                }
            } catch (e: Exception) {
                rxPreferences.saveListCategory(dummyCategoryData())
            }
        }
    }
}
