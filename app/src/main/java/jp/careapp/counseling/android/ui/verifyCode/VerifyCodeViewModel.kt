package jp.careapp.counseling.android.ui.verifyCode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.base.NetworkException
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.AppApplication
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.utils.Define
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val rxPreferences: RxPreferences,
    private val keyService: KeyService,
    private val mRepository: VerifyCodeRepository
) : BaseViewModel() {

    companion object {
        val SCREEN_CODE_TOP = 0
        val SCREEN_CODE_REGISTER = 1
        val SCREEN_CODE_BAD_USER = 2
        val SCREEN_CODE_REREGISTER = 3 // withdraw
        val SCREEN_CODE_TUTORIAL = 4
    }

    var email: String? = null

    val codeScreenAfterVerify = MutableLiveData<Int>()
    var countError: Int = 0
    val numberError = MutableLiveData<Int>()

    val mActionState = SingleLiveEvent<VerifyCodeActionState>()

    init {
        countError = 0
    }

    fun sendVerifyCode(email: String, authCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.sendVerifyCode(email, authCode)
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        val token = dataResponse.token.toString()
                        val tokenExpire = dataResponse.tokenExpire.toString()
                        val password =
                            keyService.encrypt(Define.KEY_ALIAS, dataResponse.password.toString())
                                ?: ""
                        val memberCode = dataResponse.memberCode.toString()
                        mRepository.saveUserInfo(token, tokenExpire, password, memberCode)
                        mRepository.saveEmail(email)
                        withContext(Dispatchers.Main) {
                            dataResponse.status?.let { openScreen(it) }
                        }
                    }
                } catch (e: Exception) {
                    if (e is NetworkException) {
                        withContext(Dispatchers.Main) {
                            numberError.value = 0
                        }
                    } else {
                        countError++
                        withContext(Dispatchers.Main) {
                            numberError.value = countError
                        }
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun getCountNumber(): Int {
        return countError
    }

    fun sendVerifyCodeWhenEditEmail(email: String, authCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.sendVerifyCodeAfterEditEmail(email, authCode)
                    if (response.errors.isEmpty()) {
                        mRepository.saveEmail(email)
                        withContext(Dispatchers.Main) {
                            mActionState.value = VerifyCodeActionState.EditEmailSuccess
                        }
                    }
                } catch (throwable: Throwable) {
                    handleError(throwable)
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
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
}

sealed class VerifyCodeActionState {
    object EditEmailSuccess : VerifyCodeActionState()
}
