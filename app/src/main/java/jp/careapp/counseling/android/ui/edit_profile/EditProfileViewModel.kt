package jp.careapp.counseling.android.ui.edit_profile

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.model.MenuItem
import jp.careapp.counseling.android.data.model.ParamsUpdateMember
import jp.careapp.counseling.android.data.model.TypeMember
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.my_page.NMMenuItem
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
    @Assisted
    private val savedStateHandle: SavedStateHandle,
    private val mRepository: EditProfileRepository
) : BaseViewModel() {

    fun setCurrentName(name: String, params: ParamsUpdateMember) {
        savedStateHandle["name"] = name
        savedStateHandle["params"] = params
    }

    private val _paramsUpdateName = MutableLiveData<String>()
    fun setParams(name: String) {
        _paramsUpdateName.value = name
    }

    private val _updateName: LiveData<Result<ApiObjectResponse<Object>>> =
        _paramsUpdateName.switchMap { data ->
            liveData {
                emit(Result.Loading)
                savedStateHandle.get<ParamsUpdateMember>("params")?.let {
                    emit(
                        updateName(
                            ParamsUpdateMember(
                                data,
                                it.gender,
                                it.birth
                            )
                        )
                    )
                }
            }
        }
    val updateSuccess: LiveData<String> = _updateName.switchMap {
        if (it is Result.Success) {
            _paramsUpdateName
        } else {
            savedStateHandle.getLiveData("name")
        }
    }
    val updateNameLoading: LiveData<Boolean> = _updateName.map {
        it == Result.Loading
    }
    private val _paramsUpdateProfile = MutableLiveData<ParamsUpdateMember>()
    fun setParamProfile(updateMember: ParamsUpdateMember) {
        _paramsUpdateProfile.value = updateMember
    }

    private val _updateMemberProfile: LiveData<Result<ApiObjectResponse<Object>>> =
        _paramsUpdateProfile.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(updateName(ParamsUpdateMember(data.name, data.gender, data.birth)))
            }
        }
    val updateProfileSuccess: LiveData<ParamsUpdateMember> = _updateMemberProfile.switchMap {
        if (it is Result.Success) {
            _paramsUpdateProfile
        } else {
            savedStateHandle.getLiveData("params")
        }
    }
    val updateProfileLoading: LiveData<Boolean> = _updateMemberProfile.map {
        it == Result.Loading
    }

    private suspend fun updateName(params: ParamsUpdateMember): Result<ApiObjectResponse<Object>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.updateProfile(params).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _navigateToEditFieldFragmentAction = MutableLiveData<Event<EditProfile>>()
    val navigateToEditProfileFragmentAction: LiveData<Event<EditProfile>> =
        _navigateToEditFieldFragmentAction

    fun destinationEdit(editProfile: EditProfile) {
        viewModelScope.launch {
            _navigateToEditFieldFragmentAction.value = Event(editProfile)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_updateName) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_updateMemberProfile) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error

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
}

enum class DestinationEdit {
    NAME,
    GENDER,
    AGE,
    MAIL,
}

data class EditProfile(
    val data: String,
    val action: TypeMember,
    val destination: DestinationEdit
)

sealed class EditProfileActionState {

}
