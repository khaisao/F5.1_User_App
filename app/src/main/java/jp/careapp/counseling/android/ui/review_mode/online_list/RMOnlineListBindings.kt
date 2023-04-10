package jp.careapp.counseling.android.ui.review_mode.online_list

import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.BasePerformerResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.android.utils.RMCallStatus
import jp.careapp.counseling.android.utils.RMPresenceStatus

@BindingAdapter("sttPerformerResponse")
fun TextView.setPresenceStatus(sttPerformerResponse: BasePerformerResponse?) {
    sttPerformerResponse ?: return

    when (sttPerformerResponse) {
        is RMPerformerResponse -> {
            when (sttPerformerResponse.presenceStatus) {
                RMPresenceStatus.AWAY -> {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_inactive)
                }
                RMPresenceStatus.ACCEPTING -> {
                    setTextColor(ContextCompat.getColor(context, R.color.color_00CD5C))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_active)
                }
                else -> {}
            }
        }
        is RMFavoriteResponse -> {
            when (sttPerformerResponse.presenceStatus) {
                RMPresenceStatus.AWAY -> {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_inactive)
                }
                RMPresenceStatus.ACCEPTING -> {
                    setTextColor(ContextCompat.getColor(context, R.color.color_00CD5C))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_active)
                }
                else -> {}
            }
        }
        else -> isVisible = false
    }
}

@BindingAdapter("msgPerformerResponse")
fun AppCompatImageView.setPresenceStatus(presenceStatus: Int) {
    when (presenceStatus) {
        RMPresenceStatus.AWAY -> isVisible = false
        RMPresenceStatus.ACCEPTING -> isVisible = true
    }
}

@BindingAdapter("cameraPerformerResponse")
fun AppCompatImageView.setCallStatus(chatStatus: Int) {
    when (chatStatus) {
        RMCallStatus.WAITING_OFFLINE, RMCallStatus.INCOMING_CALL -> isVisible = true
        RMCallStatus.OFFLINE -> isVisible = false
    }
}

@BindingAdapter("iv_status_online")
fun AppCompatImageView.setIvStatusOnline(presenceStatus: Int) {
    when (presenceStatus) {
        RMPresenceStatus.ACCEPTING -> Glide.with(this).load(R.drawable.bg_rm_online_status)
            .into(this)
        RMPresenceStatus.AWAY -> Glide.with(this).load(R.drawable.bg_rm_offline_status).into(this)
    }
}
