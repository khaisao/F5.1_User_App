package jp.slapp.android.android.ui.edit_mail

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

    private var userEmail = ""

    private val _userEmailLiveData = MutableLiveData<String>()
    val userEmailLiveData: LiveData<String>
        get() = _userEmailLiveData

    private val _isEnableButtonSave = MutableLiveData<Boolean>()
    val isEnableButtonSave: LiveData<Boolean>
        get() = _isEnableButtonSave

    private val _isShowWrongFormatEmail = MutableLiveData<Boolean>()
    val isShowWrongFormatEmail: LiveData<Boolean>
        get() = _isShowWrongFormatEmail

    val mActionState = SingleLiveEvent<EditMailActionState>()

    fun getEmail() {
        userEmail = mRepository.getMemberNickMail().toString()
        _userEmailLiveData.value = mRepository.getMemberNickMail()
    }

    fun checkValidEmail(userEmail: String) {
        if (this.userEmail == userEmail) {
            _isEnableButtonSave.value = false
            _isShowWrongFormatEmail.value = false
        } else {
            if (userEmail.isValidEmail()) {
                _isEnableButtonSave.value = true
                _isShowWrongFormatEmail.value = false
            } else {
                _isEnableButtonSave.value = false
                _isShowWrongFormatEmail.value = true
            }
        }
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
}

sealed class EditMailActionState {
    class EditMailSuccess(val memberMail: String) : EditMailActionState()
}
