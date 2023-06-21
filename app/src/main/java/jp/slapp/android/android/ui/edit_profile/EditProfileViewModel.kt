package jp.slapp.android.android.ui.edit_profile

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
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val mRepository: EditProfileRepository
) : BaseViewModel() {

    private val _memberName = MutableLiveData<String>()
    val memberName: LiveData<String>
        get() = _memberName

    private val _memberAge = MutableLiveData<String>()
    val memberAge: LiveData<String>
        get() = _memberAge

    private val _memberMail = MutableLiveData<String>()
    val memberMail: LiveData<String>
        get() = _memberMail

    val mActionState = SingleLiveEvent<EditProfileActionState>()

    fun getData() {
        _memberName.value = mRepository.getMemberNickName()
        _memberMail.value = mRepository.getMemberMail()
        showMemberAge(mRepository.getMemberBirth().toString())
    }

    fun editMemberBirth(memberBirth: String, year: Int, month: Int, day: Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.editMemberBirth(memberBirth)
                    if (response.errors.isEmpty()) {
                        val age = Period.between(
                            LocalDate.of(year, month, day),
                            LocalDate.now()
                        ).years
                        mRepository.saveMemberBirth(memberBirth)
                        mRepository.saveMemberAge(age)
                        withContext(Dispatchers.Main) {
                            showMemberAge(memberBirth)
                            mActionState.value = EditProfileActionState.UpdateMemberBirthSuccess
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

    private fun showMemberAge(memberBirth: String) {
        if (memberBirth == "1900-01-01") {
            _memberAge.value = "未設定"
        } else {
            _memberAge.value = "${mRepository.getMemberAge()}歳"
        }
    }
}

sealed class EditProfileActionState {
    object UpdateMemberBirthSuccess : EditProfileActionState()
}
