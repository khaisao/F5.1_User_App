package jp.careapp.counseling.android.ui.splash

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.* // ktlint-disable no-wildcard-imports
import jp.careapp.counseling.android.data.network.*
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.dummyCategoryData
import jp.careapp.counseling.android.utils.dummyFreeTemplateData
import kotlinx.coroutines.*
import java.lang.Runnable

const val DURATION_SPLASH = 1000L

class SplashViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    companion object {
        val SCREEN_CODE_TOP = 0
        val SCREEN_CODE_START = 1
        val SCREEN_CODE_BAD_USER = 2
        val SCREEN_CODE_REREGISTER = 3
    }

    val actionSPlash = SingleLiveEvent<SplashActionState>()

    val screenCode = MutableLiveData<Int>()

    private val handler = Handler(Looper.getMainLooper())

    val memberResult = MutableLiveData<ApiObjectResponse<MemberResponse>>()

    val loginResult = MutableLiveData<ApiObjectResponse<LoginResponse>>()

    private val runnable = Runnable {
        actionSPlash.value = SplashActionState.Finish
    }

    private fun openStartScreen() {
        screenCode.value = SCREEN_CODE_START
    }

    init {
        saveListCategory()
        saveListFreeTemplate()
        handler.postDelayed(runnable, DURATION_SPLASH)
    }

    fun handleActionSplash() {
        if (rxPreferences != null && !TextUtils.isEmpty(rxPreferences.getToken())) {
            if (getCurrentDateTime() != null &&
                rxPreferences!!.getTokenExpire()!!.toJPDate(DateUtil.DATE_FORMAT_1) != null &&
                getCurrentDateTime()!!.after(
                    rxPreferences!!.getTokenExpire()!!.toJPDate(DateUtil.DATE_FORMAT_1)
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
                            if (userResponse.memberCode != null) userResponse.memberCode else ""
                        userResponse.let {
                            rxPreferences.saveUserInfor(
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
                        it.dataResponse.let {
                            val status = it!!.status
                            when (status) {
                                Constants.MemberStatus.NOMARL.index -> {
                                    screenCode.value = SCREEN_CODE_TOP
                                }
                                Constants.MemberStatus.UNREGISTED.index -> {
                                    screenCode.value = SCREEN_CODE_START
                                }
                                Constants.MemberStatus.BAD.index -> {
                                    screenCode.value = SCREEN_CODE_BAD_USER
                                }
                                Constants.MemberStatus.WITHDRAWAL.index -> {
                                    screenCode.value = SCREEN_CODE_REREGISTER
                                }
                            }
                            if (it.newsLastViewDateTime != null) {
                                rxPreferences.saveNewLastViewDateTime(it.newsLastViewDateTime)
                            }
                            if (it.mail != null)
                                rxPreferences.saveEmail(it.mail)
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
