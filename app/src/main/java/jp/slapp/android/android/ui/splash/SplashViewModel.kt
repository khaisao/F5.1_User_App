package jp.slapp.android.android.ui.splash

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.Constants
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.core.utils.getCurrentDateTime
import jp.careapp.core.utils.toDate
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.LoginResponse
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.Define.Companion.NORMAL_MODE
import jp.slapp.android.android.utils.MODE_USER.Companion.MEMBER_APP_REVIEW_MODE
import jp.slapp.android.android.utils.dummyCategoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


const val DURATION_SPLASH = 1000L

class SplashViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val mRepository: SplashRepository,
) : BaseViewModel() {

    companion object {
        const val SCREEN_CODE_TOP = 0
        const val SCREEN_CODE_START = 1
        const val SCREEN_CODE_BAD_USER = 2
        const val SCREEN_CODE_REGISTER = 3
        const val SCREEN_CODE_BAD_USER_RM = 6
//        Remove the comment to access normal mode without using VPN
        const val SCREEN_CODE_TOP_RM = 4
        const val SCREEN_CODE_START_RM = 5
        const val SCREEN_CODE_REGISTER_RM = 7
//        const val SCREEN_CODE_TOP_RM = 0
//        const val SCREEN_CODE_START_RM = 1
//        const val SCREEN_CODE_REGISTER_RM = 3
    }

    val actionSPlash = SingleLiveEvent<SplashActionState>()

    val actionUpdate = SingleLiveEvent<Boolean>()

    val appMode = MutableLiveData<Int>()

    val screenCode = SingleLiveEvent<Int>()

    private val handler = Handler(Looper.getMainLooper())

    private val memberResult = MutableLiveData<ApiObjectResponse<MemberResponse>>()

    private val loginResult = MutableLiveData<ApiObjectResponse<LoginResponse>>()

    private val runnable = Runnable {
        actionSPlash.value = SplashActionState.Finish
    }

    private fun openStartScreen() {
        screenCode.value =
            if (appMode.value == NORMAL_MODE) SCREEN_CODE_START
            else SCREEN_CODE_START_RM
    }

    init {
        saveListCategory()
        handler.postDelayed(runnable, DURATION_SPLASH)
    }

    private fun tokenIsInvalid(): Boolean {
        if (getCurrentDateTime() != null
            && mRepository.getUserTokenExpire()!!.toDate(DateUtil.DATE_FORMAT_1) != null
            && getCurrentDateTime()!!.after(
                mRepository.getUserTokenExpire()!!.toDate(DateUtil.DATE_FORMAT_1)
            )
        ) {
            return true
        }
        return false
    }

    private fun handleActionSplash() {
        if (!TextUtils.isEmpty(rxPreferences.getToken())) {
            if (tokenIsInvalid()) {
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

    fun getAppMode() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val appModeResponse = apiInterface.getAppMode()
                appModeResponse.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { response ->
                            appMode.value = response.appMode
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    fun checkModeChange() {
        val oldMode = rxPreferences.getAppMode()
        if (oldMode != appMode.value) {
            rxPreferences.switchMode()
            appMode.value?.let { rxPreferences.setAppMode(it) }
        }
        handleActionSplash()
    }

    private fun login() {
        viewModelScope.launch {
            isLoading.value = true
            val deviceName = Build.MODEL
            try {
                if(!rxPreferences.getEmail().isNullOrEmpty() && !rxPreferences.getPassword().isNullOrEmpty()){
                    loginResult.value = apiInterface.login(
                        rxPreferences.getEmail()!!,
                        rxPreferences.getPassword()!!,
                        deviceName
                    )
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

    private fun isNeedUpdate(currentVersion: String, apiAppVersion: String): Boolean {
        try {
            val currentVersionSplit = currentVersion.split(".").toMutableList()
            val apiAppVersionSplit = apiAppVersion.split(".").toMutableList()
            while (currentVersionSplit.size < 3) {
                currentVersionSplit.add("0")
            }
            while (apiAppVersionSplit.size < 3) {
                apiAppVersionSplit.add("0")
            }
            if (currentVersionSplit[0].toInt() < apiAppVersionSplit[0].toInt()) {
                return true
            } else if (currentVersionSplit[0].toInt() > apiAppVersionSplit[0].toInt()) {
                return false
            }
            if (currentVersionSplit[1].toInt() < apiAppVersionSplit[1].toInt()) {
                return true
            } else if (currentVersionSplit[1].toInt() > apiAppVersionSplit[1].toInt()) {
                return false
            }
            if (currentVersionSplit[2].toInt() < apiAppVersionSplit[2].toInt()) {
                return true
            } else if (currentVersionSplit[2].toInt() > apiAppVersionSplit[2].toInt()) {
                return false
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private fun getMemberResult() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val memberDeffer = async { apiInterface.getMember() }
                val apiVersionAppDeffer = async { apiInterface.getAppVersion() }
                val memberResponse = memberDeffer.await()
                val apiVersionAppResponse = apiVersionAppDeffer.await()
                memberResult.value = memberResponse
                if (apiVersionAppResponse.errors.isEmpty() && memberResponse.errors.isEmpty()) {
                    if(!isNeedUpdate(BuildConfig.VERSION_NAME, apiVersionAppResponse.dataResponse.appVersion)){
                        when (memberResponse.dataResponse.status) {
                            Constants.MemberStatus.NOMARL.index, Constants.MemberStatus.STAFF.index -> {
                                if (appMode.value == NORMAL_MODE) {
                                    screenCode.value =
                                        if (memberResponse.dataResponse.disPlay == MEMBER_APP_REVIEW_MODE) {
                                            SCREEN_CODE_TOP_RM
                                        } else {
                                            SCREEN_CODE_TOP
                                        }
                                } else {
                                    screenCode.value = SCREEN_CODE_TOP_RM
                                }
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
                        memberResponse.dataResponse.newsLastViewDateTime?.let { it ->
                            rxPreferences.saveNewLastViewDateTime(
                                it
                            )
                        }
                        rxPreferences.saveEmail(memberResponse.dataResponse.mail)
                    } else {
                        actionUpdate.postValue(true)
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

}

sealed class SplashActionState {
    object Finish : SplashActionState()
}
