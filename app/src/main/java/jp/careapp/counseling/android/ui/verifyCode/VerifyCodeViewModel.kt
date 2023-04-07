package jp.careapp.counseling.android.ui.verifyCode

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.base.NetworkException
import jp.careapp.counseling.R
import jp.careapp.counseling.android.AppApplication
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.InforUserResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.Define
import kotlinx.coroutines.launch
import retrofit2.HttpException

class VerifyCodeViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val keyService: KeyService
) : BaseViewModel() {

    companion object {
        val SCREEN_CODE_TOP = 0
        val SCREEN_CODE_REGISTER = 1
        val SCREEN_CODE_BAD_USER = 2
        val SCREEN_CODE_REREGISTER = 3 // withdraw
        val SCREEN_CODE_TUTORIAL = 4
        val SCREEN_CODE_SELECT_CATEGORY = 5
    }

    var email: String? = null

    private val memberInforResult = MutableLiveData<ApiObjectResponse<InforUserResponse>>()

    val codeScreenAfterVerify = MutableLiveData<Int>()
    var countError: Int = 0
    val numberError = MutableLiveData<Int>()

    var isUpdateEmailSuccess = MutableLiveData(false)

    init {
        countError = 0
    }

    fun sendVerifyCode(email: String, authCode: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                memberInforResult.value =
                    email?.let { apiInterface.sendVerifyCode(it, authCode) }
                memberInforResult.value?.let {
                    if (it.errors.isEmpty()) {
                        val userResponse = it.dataResponse
                        userResponse.let {
                            val token = userResponse.token
                            val tokenExpire = userResponse.tokenExpire
                            val passWord =
                                keyService.encrypt(Define.KEY_ALIAS, userResponse.password) ?: ""
                            val memberCode = userResponse.memberCode
                            rxPreferences.saveUserInfo(token, tokenExpire, passWord, memberCode)
                            openScreen(userResponse.status)
                        }
                        saveEmail(email)
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                if (e is NetworkException) {
                    numberError.value = 0
                } else {
                    countError++
                    numberError.value = countError
                }
                isLoading.value = false
            }
        }
    }

    fun getCountNumber(): Int {
        return countError
    }

    fun sendVerifyCodeWhenEditEmail(emailEdit: String, authCode: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result =
                    emailEdit.let { apiInterface.sendVerifyCodeAfterEditEmail(it, authCode) }
                if (result.errors.isEmpty()) {
                    saveEmail(emailEdit)
                }
                isUpdateEmailSuccess.value = true
                isLoading.value = false
            } catch (throwable: Throwable) {
                handleError(throwable)
                isUpdateEmailSuccess.value = false
                isLoading.value = false
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        if (throwable is HttpException) {
            countError++
            val httpException = throwable as HttpException
            try {
                val errorBody = httpException.response()!!.errorBody()!!.string()
                val gson = GsonBuilder().create()
                val apiObjectResponse =
                    gson.fromJson(errorBody, ApiObjectResponse::class.java)
                if (apiObjectResponse != null && apiObjectResponse.errors.isNotEmpty()) {
                    val errorMessage = StringBuilder()
                    apiObjectResponse.errors.forEach {
                        errorMessage.append(" ").append(it)
                    }
                    if (AppApplication.getAppContext()
                        ?.getString(R.string.duplicate_email_address) == errorMessage.toString()
                            .trim()
                    ) {
                        numberError.value = 0
                    } else {
                        numberError.value = countError
                    }
                }
            } catch (e: Exception) {
            }
        } else if (throwable is NetworkException) {
            numberError.value = 0
        }
    }

    private fun openScreen(status: Int) {
        when (status) {
            0 -> {
                if (rxPreferences.isFirstTimeUse()) {
                    codeScreenAfterVerify.value = SCREEN_CODE_TUTORIAL
                    rxPreferences.setIsFirstTimeUseLab(true)
                    rxPreferences.setIsFirstTimeUse(false)
                } else {
                    codeScreenAfterVerify.value = SCREEN_CODE_TOP
                }
            }
            1 -> codeScreenAfterVerify.value = SCREEN_CODE_REGISTER
            2 -> codeScreenAfterVerify.value = SCREEN_CODE_BAD_USER
            3 -> codeScreenAfterVerify.value = SCREEN_CODE_REREGISTER
        }
    }

    private fun saveEmail(email: String) {
        rxPreferences.saveEmail(email)
    }
}
