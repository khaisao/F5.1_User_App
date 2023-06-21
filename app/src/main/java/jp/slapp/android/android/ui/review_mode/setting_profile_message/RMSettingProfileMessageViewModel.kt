package jp.slapp.android.android.ui.review_mode.setting_profile_message

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.slapp.android.android.utils.ActionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class RMSettingProfileMessageViewModel @ViewModelInject constructor(
    private val mRepository: RMSettingProfileMessageRepository
) : BaseViewModel() {

    val actionState = SingleLiveEvent<ActionState>()

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    init {
        val content = mRepository.getProfileMessagePreferences()
        content?.let { _content.value = it }
    }

    fun saveProfileMessage(profileMessage: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.saveProfileMessage(profileMessage)
                    if (response.errors.isEmpty()) {
                        mRepository.saveProfileMessagePreferences(profileMessage)
                        withContext(Dispatchers.Main) {
                            _content.value = profileMessage
                            actionState.value = ActionState.SaveProfileMessageSuccess(true)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            actionState.value = ActionState.SaveProfileMessageSuccess(false)
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