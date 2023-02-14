package jp.careapp.counseling.android.ui.reRegister

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch

class ReRegisterViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    var isSuccess = MutableLiveData(false)

    fun reRegister() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                rxPreferences.getEmail()?.let {
                    rxPreferences.getMemberCode()?.let { it1 ->
                        apiInterface.reRegister(it, it1)
                    }
                }
                isSuccess.value = true
                isLoading.value = false
            } catch (throwable: Throwable) {
                isSuccess.value = false
                isLoading.value = false
            }
        }
    }
}
