package jp.careapp.counseling.android.ui.delete_account

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.dummyCategoryData
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.launch

class DeleteAccountViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private val _deleteAccount = MutableLiveData<Result<ApiObjectResponse<Any>>>()
    val deleteAccount: LiveData<Result<ApiObjectResponse<Any>>> = _deleteAccount

    fun deleteAccount(reason: String) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                apiService.deleteAccount(reason).let {
                    if (it.errors.isEmpty()) {
                        _deleteAccount.postValue(Result.Success(it))
                    } else {
                        _deleteAccount.postValue(Result.Error(Throwable(message = it.errors.first())))
                    }
                }
            } catch (e: Exception) {
                _deleteAccount.postValue(Result.Error(e))
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun clearLocalData() {
        val category = rxPreferences.getListCategory() ?: dummyCategoryData()
        rxPreferences.clear()
        rxPreferences.saveListCategory(category)
    }
}