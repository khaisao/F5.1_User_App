package jp.careapp.counseling.android.ui.review_mode.settingNickName

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

class RMSettingNickNameViewModel @ViewModelInject constructor(
    private val rmApiInterface: RMApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    val actionState = SingleLiveEvent<ActionState>()
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    init {
        val nickName = rxPreferences.getNickName()
        nickName?.let {
            _nickname.value = it
        }
    }

    fun saveNickName(nickName: String) {
        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = rmApiInterface.updateNickName(nickName)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (response.errors.isEmpty()) {
                        rxPreferences.setNickName(nickName)
                        _nickname.value = nickName
                        actionState.value = ActionState.SaveNickNameSuccess(true)
                    } else {
                        actionState.value = ActionState.SaveNickNameSuccess(false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading.value = false
        }
    }
}