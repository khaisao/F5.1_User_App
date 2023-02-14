package jp.careapp.counseling.android.ui.email
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch

class InputAndEditMailViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    var isComeBackFromBackGround = MutableLiveData(false)

    fun setComebackFromBackGround(isComback: Boolean) {
        isComeBackFromBackGround.value = isComback
    }

    companion object {
        val SCREEN_LOGIN_WITH_EMAIL = 0
        val SCREEN_EDIT_EMAIL = 1
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
