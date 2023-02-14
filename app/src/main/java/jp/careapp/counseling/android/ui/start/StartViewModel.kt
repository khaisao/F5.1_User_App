package jp.careapp.counseling.android.ui.start

import android.os.Build
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.splash.SplashViewModel
import kotlinx.coroutines.launch

class StartViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    val screenCode = MutableLiveData<Int>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            val deviceName = Build.MODEL
            try {
                val response = apiInterface.login(email, password, deviceName)
                response?.let {
                    if (it.errors.isEmpty()) {
                        val userResponse = it.dataResponse
                        val memberCode = userResponse.memberCode
                        userResponse.let {
                            rxPreferences.saveUserInfor(
                                userResponse.token,
                                userResponse.tokenExpire,
                                password,
                                memberCode
                            )
                            screenCode.value = SplashViewModel.SCREEN_CODE_TOP
                        }
                    } else {
                        rxPreferences.clear()
                        screenCode.value = SplashViewModel.SCREEN_CODE_START
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                rxPreferences.clear()
                screenCode.value = SplashViewModel.SCREEN_CODE_START
                isLoading.value = false
            }
        }
    }

}
