package jp.careapp.counseling.android.ui.review_mode.settingNickName

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
class RMSettingNickNameViewModel @Inject constructor(
    private val mRepository: RmSettingNickNameRepository
) : BaseViewModel() {

    val mActionState = SingleLiveEvent<RMSettingNickNameActionState>()

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    init {
        val nickName = mRepository.getNickNamePreferences()
        nickName?.let { _nickname.value = it }
    }

    fun updateNickName(nickName: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.updateNickName(nickName)
                    if (response.errors.isEmpty()) {
                        mRepository.saveNickNamePreferences(nickName)
                        withContext(Dispatchers.Main) {
                            _nickname.value = nickName
                            mActionState.value = RMSettingNickNameActionState.UpdateNickNameSuccess
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        }
    }
}

sealed class RMSettingNickNameActionState {
    object UpdateNickNameSuccess : RMSettingNickNameActionState()
}