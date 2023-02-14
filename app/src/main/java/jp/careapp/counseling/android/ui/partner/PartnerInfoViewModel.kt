package jp.careapp.counseling.android.ui.partner

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

const val TAG = "PartnerInfoViewModel"

class PartnerInfoViewModel @ViewModelInject constructor(
    private val rxPreferences: RxPreferences,
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    private val _actionState = MutableLiveData<ActionState>()
    val actionState: MutableLiveData<ActionState> = _actionState

    private var name: String = ""
    private var gender: Int = 0
    private var birthday: String = ""

    fun setPartnerName(name: String) {
        this.name = name
    }

    fun setGender(gender: Int) {
        this.gender = gender
    }

    fun setBirthday(birthday: String) {
        this.birthday = birthday
    }

    fun updatePartnerInfo() {
        if (name.isEmpty() && gender == 0 && birthday.isEmpty()) {
            openNextScreen()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    isLoading.postValue(true)
                    val params = HashMap<String, Any>().also {
                        it["partner_name"] = name
                        if (gender > 0) {
                            it["partner_sex"] = gender
                        }
                        it["partner_birth"] = birthday
                    }
                    apiInterface.updatePartnerInfo(params).apply {
                        if (errors.isEmpty()) {
                            openNextScreen()
                        } else {
                            withContext(Dispatchers.Main) {
                                messageError.value = errors[0]
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e)
                    withContext(Dispatchers.Main) {
                        messageError.postValue(e.message)
                    }
                } finally {
                    isLoading.postValue(false)
                }
            }
        }
    }

    private fun openNextScreen() {
        if (rxPreferences.isFirstTimeUse()) {
            rxPreferences.setIsFirstTimeUse(false)
            _actionState.postValue(ActionState.OpenTutorialScreen)
        } else {
            _actionState.postValue(ActionState.OpenTopScreen)
        }
    }
}

sealed class ActionState {
    object OpenTutorialScreen : ActionState()
    object OpenTopScreen : ActionState()
}