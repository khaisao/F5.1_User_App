package jp.careapp.counseling.android.ui.profile.trouble_sheet

import android.app.Activity
import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.convertStringToCalendar
import jp.careapp.core.utils.DateUtil.Companion.getAge
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.model.InfoRegistrationWithoutEmailRequest
import jp.careapp.counseling.android.data.model.InforRegistrationRequest
import jp.careapp.counseling.android.data.model.user_profile.TroubleSheetRequest
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.TroubleSheetResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeViewModel
import jp.careapp.counseling.android.utils.AdjustUtils
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.appsflyer.AppsFlyerUtil
import kotlinx.coroutines.launch
import java.util.*


class TroubleSheetViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val keyService: KeyService
) :
    BaseViewModel() {
    val statusSendTrouble = SingleLiveEvent<Boolean>()
    val memberInFoResult = MutableLiveData<MemberResponse?>()
    val codeScreenAfterRegistration = MutableLiveData<Int>()
    val statusUpdateGenre = MutableLiveData<Int>()
    private var memberCode: String = ""

    fun sendTroubleSheet(troubleSheetRequest: TroubleSheetRequest, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.sendTroubleSheet(troubleSheetRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        statusSendTrouble.value = true
                        rxPreferences.saveTroubleSheet(
                            TroubleSheetResponse(
                                troubleSheetRequest.content,
                                troubleSheetRequest.response,
                                troubleSheetRequest.reply
                            )
                        )
                    } else {
                        statusSendTrouble.value = false
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                statusSendTrouble.value = false
                isLoading.value = false
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        sendTroubleSheet(troubleSheetRequest, activity)
                    }
                )
            }
        }
    }

    fun getMemberInfo(activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { member ->
                            memberInFoResult.value = member
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        getMemberInfo(
                            activity
                        )
                    }
                )
            }
        }
    }

    fun updateGenre(genre: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiInterface.updateGenre(genre)
                response.let {
                    if (it.errors.isEmpty()) {
                        statusUpdateGenre.value = VerifyCodeViewModel.SCREEN_CODE_TUTORIAL
                        rxPreferences.setGenre(genre)
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    fun registerWithoutEmail(request: InfoRegistrationWithoutEmailRequest) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                apiInterface.registerWithoutEmail(request).let {
                    if (it.errors.isEmpty()) {
                        val userResponse = it.dataResponse
                        userResponse.let {
                            rxPreferences.saveUserInfo(
                                userResponse.token,
                                userResponse.tokenExpire,
                                keyService.encrypt(Define.KEY_ALIAS, userResponse.passWord) ?: "",
                                userResponse.memberCode
                            )
                            rxPreferences.saveEmail(
                                userResponse.email
                            )
                            memberCode = userResponse.memberCode
                        }
                        logEventRegistration(request.birth)
                        openScreen(true)
                        rxPreferences.setFirstRegister(true)
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    fun register(registrationRequest: InforRegistrationRequest) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                apiInterface.register(registrationRequest).let {
                    if (it.errors.isEmpty()) {
                        val userResponse = it.dataResponse
                        userResponse.let {
                            rxPreferences.saveUserInfo(
                                userResponse.token,
                                userResponse.tokenExpire,
                                keyService.encrypt(Define.KEY_ALIAS, userResponse.passWord) ?: "",
                                userResponse.memberCode
                            )
                            memberCode = userResponse.memberCode
                        }
                        logEventRegistration(registrationRequest.birth)
                        openScreen(true)
                        rxPreferences.setFirstRegister(true)
                    }
                }
            } catch (throwable: Throwable) {

            } finally {
                isLoading.value = false
            }
        }
    }

    private fun logEventRegistration(birth: String) {
        // log firebase event registration_over20 if age >= 20
        val age = getAge(
            convertStringToCalendar(birth, DateUtil.DATE_FORMAT_3)
                ?: Calendar.getInstance()
        )
        if (age >= 20) {
            val bundle = Bundle().apply { putInt("age", age) }
            Firebase.analytics.logEvent("registration_over_20", bundle)
        }
    }

    private fun openScreen(isSuccess: Boolean) {
        if (isSuccess) {
            AdjustUtils.trackEventRegistration(memberCode)
            AppsFlyerUtil.handleEventRegistration(memberCode)
            codeScreenAfterRegistration.value = VerifyCodeViewModel.SCREEN_CODE_SELECT_CATEGORY
        }
    }

    fun trackEventToken() {
        AdjustUtils.trackEventInAppToken()
    }
}
