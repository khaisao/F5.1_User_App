package jp.slapp.android.android.utils.appsflyer

import android.content.Context
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import jp.slapp.android.BuildConfig
import timber.log.Timber
import kotlin.collections.HashMap

class AppsFlyerUtil {

    companion object {
        private const val AF_DEV_KEY = BuildConfig.AF_DEV_KEY;
        private const val TAG = "Appsflyer"

        private lateinit var appsFlyer: AppsFlyerLib
        private lateinit var context: Context

        fun createAppsFlyer(context: Context) {
            this.context = context
            appsFlyer = AppsFlyerLib.getInstance()
            appsFlyer.setAppId(BuildConfig.APPS_FLYER_APP_ID)
            appsFlyer.init(AF_DEV_KEY, null, context)
            appsFlyer.start(context)
        }

        fun setDebugLog() {
            appsFlyer.setDebugLog(true)
        }

        fun logEvent(eventName: String,
                     eventValues: HashMap<String, Any>) {

            appsFlyer.logEvent(context, eventName, eventValues, object : AppsFlyerRequestListener {
                override fun onSuccess() {
                    Timber.d(" $TAG Event sent successfully")
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                    Timber.d(" $TAG Event failed to be sent:\n" +
                            "Error code: " + errorCode + "\n"
                            + "Error description: " + errorDesc)
                }
            })
        }

        fun handleEventRegistration(memberCode: String) {
            appsFlyer.setCustomerUserId(memberCode)
            val eventValues = HashMap<String, Any>()
            eventValues.put(AFInAppEventParameterName.CONTENT, memberCode)
            logEvent(AFInAppEventType.COMPLETE_REGISTRATION, eventValues)
        }
    }
}