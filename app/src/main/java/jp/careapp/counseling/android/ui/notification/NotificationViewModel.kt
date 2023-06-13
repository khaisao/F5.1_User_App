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

const val PUSH_DO_NOT_RECEIVE_NOTIFICATION = 0
const val PUSH_RECEIVE_NOTIFICATION = 1

const val PUSH_DO_NOT_RECEIVE_MAIL = 0
const val PUSH_RECEIVE_MAIL = 1

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val mRepository: NotificationRepository
) : BaseViewModel() {

    private val _statusSwitch = MutableLiveData<Int>()
    val statusSwitch: LiveData<Int>
        get() = _statusSwitch

    private val _statusSwitchReceiveMail = MutableLiveData<Int>()
    val statusSwitchReceiveMail: LiveData<Int>
        get() = _statusSwitchReceiveMail

    init {
        _statusSwitch.value = mRepository.getMemberSettingNotification()
        _statusSwitchReceiveMail.value = isTurnOnNoticeMailAndNewsLetterNoticeMail()
    }

    private fun isTurnOnNoticeMailAndNewsLetterNoticeMail(): Int {
        return if (mRepository.getMemberSettingReceiveNoticeMail() == 1 && mRepository.getMemberSettingReceiveNewsLetterNoticeMail() == 1) {
            1
        } else {
            0
        }
    }

    fun updateSettingNotification(isChecked: Boolean) {
        isLoading.value = true
        val statusNotification = if (isChecked) PUSH_RECEIVE_NOTIFICATION else PUSH_DO_NOT_RECEIVE_NOTIFICATION
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response =
                        mRepository.updateNotification(
                            UpdateNotificationParams(
                                pushMail = statusNotification,
                                receiveNewsletterMail = mRepository.getMemberSettingReceiveNewsLetterNoticeMail(),
                                receiverNoticeMail = mRepository.getMemberSettingReceiveNoticeMail()
                            )
                        )
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

    fun updateSettingMail(isChecked: Boolean) {
        isLoading.value = true
        val statusMail = if (isChecked) PUSH_RECEIVE_MAIL else PUSH_DO_NOT_RECEIVE_MAIL
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response =
                        mRepository.updateNotification(UpdateNotificationParams(
                            receiveNewsletterMail = statusMail,
                            pushMail = mRepository.getMemberSettingNotification(),
                            receiverNoticeMail = statusMail
                        ))
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mRepository.saveMemberSettingReceiveNoticeMail(statusMail)
                            mRepository.saveMemberSettingReceiveNewsLetterNoticeMail(statusMail)
                            _statusSwitchReceiveMail.value = statusMail
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
