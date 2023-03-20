package jp.careapp.counseling.android.ui.edit_mail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.core.utils.StringUtils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditMailViewModel @Inject constructor(private val mRepository: EditMailRepository) :
    BaseViewModel() {

    private val _memberMail = MutableLiveData<String>()
    val memberMail: LiveData<String>
        get() = _memberMail

    private val _isValidMail = MutableLiveData<Boolean>()
    val isValidMail: LiveData<Boolean>
        get() = _isValidMail

    val mActionState = SingleLiveEvent<EditMailActionState>()

    fun getEmail() {
        _memberMail.value = mRepository.getMemberNickMail()
    }

    fun updateMail(memberMail: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.updateMail(memberMail)
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mActionState.value = EditMailActionState.EditMailSuccess(memberMail)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun handleEdtMail(mail: String) {
        _isValidMail.value = !mail.isValidEmail()
    }
}

sealed class EditMailActionState {
    class EditMailSuccess(val memberMail: String) : EditMailActionState()
}
