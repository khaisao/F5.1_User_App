package jp.careapp.counseling.android.ui.edit_nick_name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditNickNameViewModel @Inject constructor(private val mRepository: EditNickNameRepository) :
    BaseViewModel() {

    private var userName = ""

    private val _userNameLiveData = MutableLiveData<String>()
    val userNameLiveData: LiveData<String>
        get() = _userNameLiveData

    private val _isEnableButtonSave = MutableLiveData(false)
    val isEnableButtonSave: LiveData<Boolean>
        get() = _isEnableButtonSave

    val mActionState = SingleLiveEvent<EditNickNameActionState>()

    fun getUserName() {
        userName = mRepository.getMemberNickName().toString()
        _userNameLiveData.value = userName
    }

    fun checkEnableButtonSave(count: Int, userName: String) {
        _isEnableButtonSave.value = count != 0 && this.userName != userName
    }

    fun editMemberName(memberName: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.editMemberName(memberName)
                    if (response.errors.isEmpty()) {
                        mRepository.saveMemberName(memberName)
                        withContext(Dispatchers.Main) {
                            mActionState.value = EditNickNameActionState.EditNickNameSuccess
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

sealed class EditNickNameActionState {
    object EditNickNameSuccess : EditNickNameActionState()
}