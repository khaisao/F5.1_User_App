package jp.careapp.counseling.android.ui.edit_profile

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
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
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
        _memberAge.value = "${mRepository.getMemberAge()}歳"
    }

    fun editMemberBirth(memberBirth: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.editMemberBirth(memberBirth)
                    if (response.errors.isEmpty()) {
                        val age = Calendar.getInstance().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate().until(LocalDate.now(), ChronoUnit.YEARS).toInt()
                        mRepository.saveMemberAge(age)
                        withContext(Dispatchers.Main) {
                            _memberAge.value = "${mRepository.getMemberAge()}歳"
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
}

sealed class EditProfileActionState {
    object UpdateMemberBirthSuccess : EditProfileActionState()
}
