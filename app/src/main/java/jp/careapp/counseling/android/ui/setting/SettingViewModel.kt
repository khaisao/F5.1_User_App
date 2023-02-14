package jp.careapp.counseling.android.ui.setting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch

class SettingViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    val memberInFoResult = MutableLiveData<MemberResponse?>()

    var isEnableWithdrawal: Boolean = true
    var isOpenWithdrawal: Boolean = true

    fun loadMemberInfo() {
        viewModelScope.launch {
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            memberInFoResult.value = data
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}
