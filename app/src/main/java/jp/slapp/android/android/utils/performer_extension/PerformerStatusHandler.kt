package jp.slapp.android.android.utils.performer_extension

import android.content.res.Resources
import jp.slapp.android.R

class PerformerStatusHandler {
    companion object {
        fun getStatus(callStatus: Int, chatStatus: Int): PerformerStatus {
            return when {
                (callStatus == 1 || callStatus == 2) && chatStatus == 0 -> PerformerStatus.WAITING
                callStatus == 0 && (chatStatus == 1 || chatStatus == 2) -> PerformerStatus.LIVE_STREAM
                callStatus == 0 && chatStatus == 3 -> PerformerStatus.PRIVATE_LIVE_STREAM
                callStatus == 0 && chatStatus == 8 -> PerformerStatus.PREMIUM_PRIVATE_LIVE_STREAM
                else -> PerformerStatus.OFFLINE
            }
        }

        fun getStatusText(status: PerformerStatus, resources: Resources): String {
            return resources.getString(
                when (status) {
                    PerformerStatus.WAITING -> R.string.presence_status_waiting
                    PerformerStatus.LIVE_STREAM -> R.string.presence_status_live_streaming
                    PerformerStatus.PRIVATE_LIVE_STREAM -> R.string.presence_status_private_delivery
                    PerformerStatus.PREMIUM_PRIVATE_LIVE_STREAM -> R.string.presence_status_private_delivery
                    else -> R.string.presence_status_offline
                }
            )
        }

        fun getStatusBg(status: PerformerStatus): Int {
            return when (status) {
                PerformerStatus.WAITING -> R.drawable.bg_performer_status_waiting
                PerformerStatus.LIVE_STREAM -> R.drawable.bg_performer_status_live_streaming
                PerformerStatus.PRIVATE_LIVE_STREAM -> R.drawable.bg_performer_status_private_delivery
                PerformerStatus.PREMIUM_PRIVATE_LIVE_STREAM -> R.drawable.bg_performer_status_private_delivery
                else -> R.drawable.bg_performer_status_offline
            }
        }
    }
}

enum class PerformerStatus {
    WAITING,
    LIVE_STREAM,
    PRIVATE_LIVE_STREAM,
    PREMIUM_PRIVATE_LIVE_STREAM,
    OFFLINE
}

