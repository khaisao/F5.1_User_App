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

    private val _memberNickName = MutableLiveData<String>()
    val memberNickName: LiveData<String>
        get() = _memberNickName

    val mActionState = SingleLiveEvent<EditNickNameActionState>()

    init {
        _memberNickName.value = mRepository.getMemberNickName()
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