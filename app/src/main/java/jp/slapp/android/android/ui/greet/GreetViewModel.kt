package jp.slapp.android.android.ui.greet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch

class GreetViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    companion object {
        val SCREEN_LOGIN_WITH_EMAIL = 0
        val SCREEN_EDIT_EMAIL = 1
        val SCREEN_REGISTER_WITH_EMAIL = 2
    }

    var isSuccess = MutableLiveData(false)

    fun sendEmail(email: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                apiInterface.sendEmail(email)
                isSuccess.value = true
                isLoading.value = false
            } catch (throwable: Throwable) {
                isSuccess.value = false
                isLoading.value = false
            }
        }
    }

    fun editEmail(email: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                apiInterface.updateEmail(email)
                isSuccess.value = true
                isLoading.value = false
            } catch (throwable: Throwable) {
                isSuccess.value = false
                isLoading.value = false
            }
        }
    }
}
