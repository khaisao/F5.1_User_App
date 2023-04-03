package jp.careapp.counseling.android.ui.registration

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.provider.Settings
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.InfoRegistrationWithoutEmailRequest
import jp.careapp.counseling.android.data.model.InforRegistrationRequest
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.RegistrationResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import java.util.*

class RegistrationViewModel @ViewModelInject constructor(
    private val application: Application,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    val dateOfBirth = MutableLiveData<String>()

    val registerResult = MutableLiveData<ApiObjectResponse<RegistrationResponse>>()
    val isSuccess = MutableLiveData(false)
    private var statusPrivacyTerm: Int = 0
    private var name: String = ""
    private var sex: Int = 0
    private var receiveMail = 0
    private lateinit var mCalendar: Calendar

    val codeScreenAfterRegistration = MutableLiveData<Int>()
    private var memberCode: String = ""

    fun setPrivacyTerm(status: Int) {
        this.statusPrivacyTerm = status
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun setGender(gender: Int) {
        this.sex = gender
    }

    fun getGender(): Int {
        return this.sex
    }

    fun getCalendar(): Calendar {
        return mCalendar
    }

    fun getPrivacyTerm(): Int {
        return statusPrivacyTerm
    }

    fun setReceiveMail(isNeedReceiveMail: Boolean) {
        receiveMail = if (isNeedReceiveMail) 1 else 0
    }

    fun openDatePicker(birthDay: String, context: Context) {
        if (context != null) {
            if (birthDay.isEmpty()) {
                mCalendar = Calendar.getInstance()
                mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR) - 18)
            } else {
                mCalendar = DateUtil.convertStringToCalendar(birthDay, DateUtil.DATE_FORMAT_2)!!
            }
            val datePickerDialog = context.let {
                DatePickerDialog(
                    it,
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    date,
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)
                )
            }
            datePickerDialog.let {
                it.datePicker.maxDate = System.currentTimeMillis() - 568111536000L
                it.show()
            }
        }
    }

    var date =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat: String =
                DateUtil.getDateTimeDisplayByFormat(DateUtil.DATE_FORMAT_2, mCalendar)
            dateOfBirth.value = myFormat
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
                                userResponse.passWord,
                                userResponse.memberCode
                            )
                            memberCode = userResponse.memberCode
                        }
                        rxPreferences.setFirstRegister(true)
                    }
                }
            } catch (throwable: Throwable) {

            } finally {
                isLoading.value = false
            }
        }
    }

    fun getRegisterRequest(): InforRegistrationRequest {
        return InforRegistrationRequest(
            token = rxPreferences.getToken().toString().trim(),
            name = name,
            sex = 1,
            receiveNewsLetterEmail = receiveMail,
            receiveNoticeMail = receiveMail,
            pushNewsletter = 1,
            pushMail = 1,
            pushOnline = 1,
            pushCounseling = 1,
            birth = application.getString(R.string.birth_default),
            androidId = getAndroidId()
        )
    }

    fun getRegisterRequestWithoutMail(): InfoRegistrationWithoutEmailRequest {
        return InfoRegistrationWithoutEmailRequest(
            name = name,
            sex = 1,
            pushNewsletter = 1,
            pushMail = 1,
            pushOnline = 1,
            pushCounseling = 1,
            birth = application.getString(R.string.birth_default),
            androidId = getAndroidId()
        )
    }

    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            application.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}
