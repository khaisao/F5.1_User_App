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
import jp.careapp.counseling.android.data.network.InfoUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

const val SCREEN_CODE_TOP = 0
const val SCREEN_CODE_REGISTER = 1

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val mRepository: VerifyCodeRepository
) : BaseViewModel() {

    var email: String? = null

    var countError: Int = 0
    val numberError = MutableLiveData<Int>()

    val mActionState = SingleLiveEvent<VerifyCodeActionState>()

    init {
        countError = 0
    }

    lateinit var dataResponseRegister : InfoUserResponse

    fun sendVerifyCode(email: String, authCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.sendVerifyCode(email, authCode)
                    if (response.errors.isEmpty()) {
                        dataResponseRegister = response.dataResponse
                        withContext(Dispatchers.Main) {
                            when (dataResponseRegister.status) {
                                SCREEN_CODE_TOP -> {
                                    val token = dataResponseRegister.token.toString()
                                    val tokenExpire = dataResponseRegister.tokenExpire.toString()
                                    val password = dataResponseRegister.password.toString()
                                    val memberCode = dataResponseRegister.memberCode.toString()
                                    mRepository.saveUserInfo(token, tokenExpire, password, memberCode)
                                    mRepository.saveEmail(email)
                                    mActionState.value =
                                        VerifyCodeActionState.NavigateToTop
                                }
                                SCREEN_CODE_REGISTER -> mActionState.value =
                                    VerifyCodeActionState.NavigateToRegister
                            }
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
            try {
                val errorBody = throwable.response()!!.errorBody()!!.string()
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
}

sealed class VerifyCodeActionState {
    object EditEmailSuccess : VerifyCodeActionState()

    object NavigateToTop : VerifyCodeActionState()

    object NavigateToRegister : VerifyCodeActionState()
}
