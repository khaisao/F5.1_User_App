package jp.careapp.counseling.android.utils

import android.content.Context
import com.adjust.sdk.* // ktlint-disable no-wildcard-imports
import timber.log.Timber

class AdjustUtils {
    companion object {
        private const val TAG = "AdjustUtils"
        const val ADJUST_APP_TOKEN = "u9iestwac3r4"
        const val ADJUST_MEMBER_CODE = "memberCode"
        const val ADJUST_EVENT_CURRENCY = "JPY"

        const val DEFAULT_TRACKER_NAME = "Organic"

        const val REGISTRATION_EVENT_TOKEN = "9h0pzd"
        const val IN_APP_PURCHASE_EVENT_TOKEN = "v9fp8y"

        fun setUpTrackingAdjust(context: Context) {
            val appToken = jp.careapp.counseling.BuildConfig.ADJUST_APP_TOKEN
            var environment: String
            if (BuildConfig.DEBUG) {
                environment = AdjustConfig.ENVIRONMENT_SANDBOX
            } else {
                environment = AdjustConfig.ENVIRONMENT_PRODUCTION
            }
            var trackerToken: String = ""
            val configAdjust = AdjustConfig(context, appToken, environment)
            configAdjust.setLogLevel(LogLevel.VERBOSE)
            configAdjust.setOnAttributionChangedListener(
                OnAttributionChangedListener {
                    trackerToken = if (it.trackerToken != null) it.trackerToken else ""
                    Timber.tag(TAG).d("Tracker Token: $trackerToken")
                }
            )

            // Set event success tracking delegate.
            configAdjust.setOnEventTrackingSucceededListener { eventSuccessResponseData ->
                Timber.tag(TAG).d("Event success data: $eventSuccessResponseData")
            }

            // Set event failure tracking delegate.
            configAdjust.setOnEventTrackingFailedListener { eventFailureResponseData ->
                Timber.tag(TAG).d("Event failure data: $eventFailureResponseData")
            }

            // Set session success tracking delegate.
            configAdjust.setOnSessionTrackingSucceededListener { sessionSuccessResponseData ->
                Timber.tag(TAG).d("Session success data: $sessionSuccessResponseData")
            }

            // Set session failure tracking delegate.
            configAdjust.setOnSessionTrackingFailedListener { sessionFailureResponseData ->
                Timber.tag(TAG).d("Session failure data: $sessionFailureResponseData")
            }

            configAdjust.setDefaultTracker(trackerToken)
            Adjust.onCreate(configAdjust)
        }

        fun trackEventRegistration(memberCode: String) {
            val adjustEvent = AdjustEvent(jp.careapp.counseling.BuildConfig.REGISTRATION_EVENT_TOKEN)
            adjustEvent.addCallbackParameter(ADJUST_MEMBER_CODE, memberCode)
            Adjust.trackEvent(adjustEvent)
            val adjustEvent2 = AdjustEvent(jp.careapp.counseling.BuildConfig.REGISTRATION_EVENT_TOKEN_2)
            adjustEvent2.addCallbackParameter(ADJUST_MEMBER_CODE, memberCode)
            Adjust.trackEvent(adjustEvent2)
        }

        fun trackEventInAppPurchase(price: Double) {
            val adjustEvent = AdjustEvent(jp.careapp.counseling.BuildConfig.IN_APP_PURCHASE_EVENT_TOKEN)
            adjustEvent.setRevenue(price, ADJUST_EVENT_CURRENCY)
            Adjust.trackEvent(adjustEvent)
        }

        fun trackEventInAppToken() {
            val adjustEvent = AdjustEvent(jp.careapp.counseling.BuildConfig.ADJUST_EVENT_TOKEN)
            Adjust.trackEvent(adjustEvent)
        }
    }
}
