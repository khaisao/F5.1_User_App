package jp.careapp.counseling.android.ui.review_mode.settingProfileMessage

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.ActionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMSettingProfileMessageViewModel @ViewModelInject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    val actionState = SingleLiveEvent<ActionState>()
    private val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    init {
        val content = rxPreferences.getContent()
        content?.let {
            _content.value = it
        }
    }

    fun saveProfileMessage(content: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = rmApiInterface.saveProfileMessage(content)
                withContext(Dispatchers.Main) {
                    response.let {
                        if (it.errors.isEmpty()) {
                            rxPreferences.setContent(content)
                            _content.value = content
                            actionState.value = ActionState.SaveProfileMessageSuccess(true)
                        } else {
                            actionState.value = ActionState.SaveProfileMessageSuccess(false)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}