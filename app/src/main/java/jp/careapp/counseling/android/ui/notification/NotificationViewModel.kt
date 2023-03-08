package jp.careapp.counseling.android.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.UpdateNotificationParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val PUSH_DO_NOT_RECEIVE = 0
const val PUSH_RECEIVE = 1

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val mRepository: NotificationRepository
) : BaseViewModel() {

    private val _statusSwitch = MutableLiveData<Int>()
    val statusSwitch: LiveData<Int>
        get() = _statusSwitch

    init {
        _statusSwitch.value = mRepository.getMemberSettingNotification()
    }

    fun updateSettingNotification(isChecked: Boolean) {
        isLoading.value = true
        val statusNotification = if (isChecked) PUSH_RECEIVE else PUSH_DO_NOT_RECEIVE
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response =
                        mRepository.updateNotification(UpdateNotificationParams(pushMail = statusNotification))
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mRepository.saveSettingNotificationNM(statusNotification)
                            _statusSwitch.value = statusNotification
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
