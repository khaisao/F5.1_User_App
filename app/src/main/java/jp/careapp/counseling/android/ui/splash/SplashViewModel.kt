package jp.careapp.counseling.android.ui.splash

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.*
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.LoginResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.dummyCategoryData
import jp.careapp.counseling.android.utils.dummyFreeTemplateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

const val DURATION_SPLASH = 1000L

class SplashViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    companion object {
        const val SCREEN_CODE_TOP = 0
        const val SCREEN_CODE_START = 1
        const val SCREEN_CODE_BAD_USER = 2
        const val SCREEN_CODE_REGISTER = 3
        const val SCREEN_CODE_TOP_RM = 4
        const val SCREEN_CODE_START_RM = 5
        const val SCREEN_CODE_BAD_USER_RM = 6
        const val SCREEN_CODE_REGISTER_RM = 7
        const val NORMAL_MODE = 1
        const val REVIEW_MODE = 98
    }

    val actionSPlash = SingleLiveEvent<SplashActionState>()

    val screenCode = MutableLiveData<Int>()

    private val handler = Handler(Looper.getMainLooper())

    private val memberResult = MutableLiveData<ApiObjectResponse<MemberResponse>>()

    private val loginResult = MutableLiveData<ApiObjectResponse<LoginResponse>>()

    private val appMode = MutableLiveData<Int>()

    private val runnable = Runnable {
        actionSPlash.value = SplashActionState.Finish
    }

    private fun openStartScreen() {
        // TODO Handle check review mode
        appMode.value = NORMAL_MODE
        screenCode.value = SCREEN_CODE_START_RM
    }

    init {
        saveListCategory()
        saveListFreeTemplate()
        handler.postDelayed(runnable, DURATION_SPLASH)
    }

    fun handleActionSplash() {
        openStartScreen()
        if (!TextUtils.isEmpty(rxPreferences.getToken())) {
            if (getCurrentDateTime() != null &&
                rxPreferences.getTokenExpire()!!.toJPDate(DateUtil.DATE_FORMAT_1) != null &&
                getCurrentDateTime()!!.after(
                    rxPreferences.getTokenExpire()!!.toJPDate(DateUtil.DATE_FORMAT_1)
                )
            ) {
                login()
            } else {
                getMemberResult()
            }
        } else {
            openStartScreen()
        }
    }

    private fun saveListCategory() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                val response = apiInterface.getListCategory()
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

    private fun saveListFreeTemplate() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                val response = apiInterface.getListTemplate()
                response.let {
                    if (it.errors.isEmpty()) {
                        rxPreferences.saveListTemplate(it.dataResponse)
                    }
                }
            } catch (e: Exception) {
                rxPreferences.saveListTemplate(dummyFreeTemplateData())
            }
        }
    }

    private fun checkAppMode() {
        // TODO Call API check mode to switch ReviewMode/NormalMode
    }

    private fun switchMode() {
        // TODO Handle clear all data of old mode
    }

    private fun login() {
        viewModelScope.launch {
            isLoading.value = true
            val deviceName = Build.MODEL
            try {
                loginResult.value = rxPreferences.getEmail()?.let {
                    apiInterface.login(it, rxPreferences.getPassword()!!, deviceName)
                }
                loginResult.value?.let {
                    if (it.errors.isEmpty()) {
                        val userResponse = it.dataResponse
                        val memberCode =
                            userResponse.memberCode
                        userResponse.let {
                            rxPreferences.saveUserInfo(
                                userResponse.token,
                                userResponse.tokenExpire,
                                rxPreferences.getPassword().toString(),
                                memberCode
                            )
                            getMemberResult()
                        }
                    } else {
                        openStartScreen()
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                openStartScreen()
                isLoading.value = false
            }
        }
    }

    private fun getMemberResult() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                memberResult.value = apiInterface.getMember()
                memberResult.value?.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { response ->
                            when (response.status) {
                                Constants.MemberStatus.NOMARL.index -> {
                                    screenCode.value =
                                        if (appMode.value == NORMAL_MODE) SCREEN_CODE_TOP else SCREEN_CODE_TOP_RM
                                }
                                Constants.MemberStatus.UNREGISTED.index -> {
                                    screenCode.value =
                                        if (appMode.value == NORMAL_MODE) SCREEN_CODE_START else SCREEN_CODE_START_RM
                                }
                                Constants.MemberStatus.BAD.index -> {
                                    screenCode.value =
                                        if (appMode.value == NORMAL_MODE) SCREEN_CODE_BAD_USER else SCREEN_CODE_BAD_USER_RM
                                }
                                Constants.MemberStatus.WITHDRAWAL.index -> {
                                    screenCode.value =
                                        if (appMode.value == NORMAL_MODE) SCREEN_CODE_REGISTER else SCREEN_CODE_REGISTER_RM
                                }
                            }
                            rxPreferences.saveNewLastViewDateTime(response.newsLastViewDateTime)
                            rxPreferences.saveEmail(response.mail)
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                login()
            }
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(runnable)
        super.onCleared()
    }

    private val _isUpdate = MutableLiveData<Boolean>(false)
    val isUpdate: LiveData<Boolean> = _isUpdate
    fun setUpdateable(isUpdate: Boolean) {
        _isUpdate.value = isUpdate
    }
}

sealed class SplashActionState {
    object Finish : SplashActionState()
}
