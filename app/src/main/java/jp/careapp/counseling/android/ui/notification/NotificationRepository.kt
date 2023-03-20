package jp.careapp.counseling.android.ui.notification

import jp.careapp.counseling.android.data.model.UpdateNotificationParams
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) {

    fun saveSettingNotificationNM(statusNotification: Int) =
        rxPreferences.saveSettingNotificationNM(statusNotification)

    fun getMemberSettingNotification() = rxPreferences.getSettingNotificationNM()

    suspend fun updateNotification(updateNotificationParams: UpdateNotificationParams) =
        apiInterface.updateNotification(updateNotificationParams)
}