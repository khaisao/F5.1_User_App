package jp.slapp.android.android.ui.notification

import jp.slapp.android.android.data.model.UpdateNotificationParams
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun saveSettingNotificationNM(statusNotification: Int) =
        rxPreferences.saveSettingNotificationNM(statusNotification)

    fun getMemberSettingNotification() = rxPreferences.getSettingNotificationNM()

    fun getMemberSettingReceiveNoticeMail() = rxPreferences.getSettingReceiveNoticeMail()

    fun getMemberSettingReceiveNewsLetterNoticeMail() = rxPreferences.getSettingReceiveNewsLetterNoticeMail()

    fun saveMemberSettingReceiveNoticeMail(status: Int) = rxPreferences.saveSettingReceiveNoticeMail(status)

    fun saveMemberSettingReceiveNewsLetterNoticeMail(status: Int) = rxPreferences.saveSettingReceiveNewsLetterNoticeMail(status)

    suspend fun updateNotification(updateNotificationParams: UpdateNotificationParams) =
        apiInterface.updateNotification(updateNotificationParams)
}