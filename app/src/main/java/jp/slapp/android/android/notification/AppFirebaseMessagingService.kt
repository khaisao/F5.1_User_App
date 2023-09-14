package jp.slapp.android.android.notification

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.AppPreferences
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.ui.main.MainActivity
import jp.slapp.android.android.utils.Define
import me.leolin.shortcutbadger.ShortcutBadger
import timber.log.Timber


class AppFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.tag(TAG).d("FCM data: ${remoteMessage.data}")

        val myProcess = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        val isInBackGround: Boolean = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        if (isInBackGround) {
            showNotification(this, remoteMessage)
        }
    }

    private fun showNotification(context: Context, remoteMessage: RemoteMessage) {
        // notify channel
        val channelId = getString(R.string.default_notification_channel_id)

        // notify sound
        val defaultSoundUri = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )

        // notify data
        val data = remoteMessage.data
        var body: String? = ""
        if (data.containsKey(Define.Intent.BODY)) {
            body = data[Define.Intent.BODY]?.trim()
        }

        var messageType: String? = null
        if (data.containsKey(Define.Intent.TYPE)) {
            messageType = data[Define.Intent.TYPE]
        }

        var performerCode = -1
        if (data.containsKey(Define.Intent.PERFORMER_CODE)) {
            val tmp = data[Define.Intent.PERFORMER_CODE]
            performerCode = try {
                tmp!!.toInt()
            } catch (ex: Exception) {
                -1
            }
        }

        var url: String? = ""
        if (data.containsKey(Define.Intent.URL)) {
            url = data[Define.Intent.URL]
            if (url != null) {
                messageType = url.split(context.getString(R.string.regex_link))[1]
            }
        }

        var numberUnread = 0
        if (messageType.equals(Define.Intent.MESSAGE)) {
            val preferences: RxPreferences = AppPreferences(context)
            numberUnread = preferences.getNumberUnreadMessage() + 1
            preferences.saveNumberUnreadMessage(numberUnread)
            val intent1 = Intent()
            intent1.action = Define.Intent.ACTION_RECEIVE_NOTIFICATION
            val bundle1 = Bundle()
            bundle1.putInt(Define.Intent.NUMBER_UNREAD_MESSAGE, numberUnread)
            intent1.putExtra(Define.Intent.DATA_FROM_NOTIFICATION, bundle1)
            context.sendBroadcast(intent1)
        }

        var questionId = -1
        if (data.containsKey(Define.Intent.QUESTION_ID)) {
            val tmp = data[Define.Intent.QUESTION_ID]
            questionId = try {
                tmp!!.toInt()
            } catch (ex: Exception) {
                -1
            }
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.action = java.lang.Long.toString(System.currentTimeMillis())

        val bundle = Bundle()
        bundle.putString(Define.Intent.BODY, body)
        if (!url.isNullOrEmpty()) {
            bundle.putString(Define.Intent.URL, url)
        }
        bundle.putString(Define.Intent.TYPE, messageType)
        bundle.putInt(Define.Intent.PERFORMER_CODE, performerCode)
        if (questionId > 0) {
            bundle.putInt(Define.Intent.QUESTION_ID, questionId)
        }
        intent.putExtra(Define.Intent.DATA_FROM_NOTIFICATION, bundle)

        val resultPendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.iv_splash)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent)

        notificationBuilder.setVibrate(longArrayOf(100, 100))

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(
            channelId,
            resources.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)
        notificationBuilder.priority = NotificationManager.IMPORTANCE_HIGH
        notificationBuilder.setChannelId(channelId)

        try {
            ShortcutBadger.applyCount(applicationContext, numberUnread)
        } catch (e: java.lang.Exception) {
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.tag(TAG).d("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Timber.tag(TAG).d("sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    companion object {

        private const val TAG = "AppFirebaseMessagingService"
    }
}
