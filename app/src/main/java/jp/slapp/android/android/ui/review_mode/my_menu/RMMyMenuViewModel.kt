package jp.slapp.android.android.ui.review_mode.my_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMMyMenuViewModel @Inject constructor(
    private val mRepository: RMMyMenuRepository
) : BaseViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userPoint = MutableLiveData<Int>()
    val userPoint: LiveData<Int>
        get() = _userPoint

    val mActionState = SingleLiveEvent<RMMyMenuActionState>()

    fun showData() {
        _userName.value = mRepository.getNickName()
        _userPoint.value = mRepository.getPoint()
    }

    fun handleWithdrawal(reason: String) {
        isLoading.value = true
        viewModelScope.launch {
            supervisorScope {
                try {
                    val response = mRepository.withdrawal(reason)
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mActionState.value = RMMyMenuActionState.WithdrawalSuccess
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
}

sealed class RMMyMenuActionState {
    object WithdrawalSuccess : RMMyMenuActionState()
}