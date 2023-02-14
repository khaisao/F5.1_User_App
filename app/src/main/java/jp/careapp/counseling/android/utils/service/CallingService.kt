package jp.careapp.counseling.android.utils.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import jp.careapp.counseling.R

class CallingService : Service() {

    private var notificationId = 0
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationChannel(
            getString(R.string.incoming_call_channel_id),
            getString(R.string.incoming_call_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
        }.let {
            notificationManager.createNotificationChannel(it)
        }

        notificationId = System.currentTimeMillis().toInt()

        NotificationCompat.Builder(this, getString(R.string.incoming_call_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_is_calling))
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build().let {
                startForeground(notificationId, it)
            }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}