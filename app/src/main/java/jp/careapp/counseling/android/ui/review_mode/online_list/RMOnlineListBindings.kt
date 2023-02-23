package jp.careapp.counseling.android.ui.review_mode.online_list

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.BasePerformerResponse
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.android.utils.RMCallStatus.WAITING_OFFLINE
import jp.careapp.counseling.android.utils.RMPresenceStatus.ACCEPTING
import jp.careapp.counseling.android.utils.RMPresenceStatus.AWAY

@BindingAdapter("sttPerformerResponse")
fun TextView.setPresenceStatus(sttPerformerResponse: BasePerformerResponse?) {
    sttPerformerResponse ?: return

    when (sttPerformerResponse) {
        is RMPerformerResponse -> {
            when (sttPerformerResponse.presenceStatus) {
                AWAY -> {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_inactive)
                }
                ACCEPTING -> {
                    setTextColor(ContextCompat.getColor(context, R.color.color_00CD5C))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_active)
                }
                else -> {}
            }
        }
        is RMFavoriteResponse -> {
            when (sttPerformerResponse.presenceStatus) {
                AWAY -> {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_online_status_inactive)
                }
                ACCEPTING -> {
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
fun ImageView.setPresenceStatus(msgPerformerResponse: BasePerformerResponse?) {
    msgPerformerResponse ?: return

    isVisible = when (msgPerformerResponse) {
        is RMPerformerResponse -> msgPerformerResponse.presenceStatus == ACCEPTING
        is RMFavoriteResponse -> msgPerformerResponse.presenceStatus == ACCEPTING
        else -> false
    }
}

@BindingAdapter("avatarPerformerResponse")
fun ImageView.setImageUrl(avatarPerformerResponse: BasePerformerResponse?) {
    avatarPerformerResponse ?: return

    val url = when (avatarPerformerResponse) {
        is RMPerformerResponse -> {
            avatarPerformerResponse.imageUrl
        }
        is RMFavoriteResponse -> {
            avatarPerformerResponse.imageUrl
        }
        is RMBlockListResponse -> {
            avatarPerformerResponse.imageUrl
        }
        else -> ""
    }
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_no_image)
        .circleCrop()
        .into(this)
}

@BindingAdapter("cameraPerformerResponse")
fun ImageView.setCallStatus(cameraPerformerResponse: BasePerformerResponse?) {
    cameraPerformerResponse ?: return

    isVisible = when (cameraPerformerResponse) {
        is RMPerformerResponse -> cameraPerformerResponse.callStatus == WAITING_OFFLINE
        is RMFavoriteResponse -> cameraPerformerResponse.callStatus == WAITING_OFFLINE
        else -> false
    }
}

@BindingAdapter("namePerformerResponse")
fun TextView.setNamePerformer(namePerformerResponse: BasePerformerResponse?) {
    namePerformerResponse ?: return

    text = when (namePerformerResponse) {
        is RMPerformerResponse -> namePerformerResponse.name
        is RMFavoriteResponse -> namePerformerResponse.name
        is RMBlockListResponse -> namePerformerResponse.name
        else -> ""
    }
}

@BindingAdapter("performerResponse")
fun Button.setVisibleRemove(performerResponse: BasePerformerResponse?) {
    performerResponse ?: return

    isVisible = when (performerResponse) {
        is RMPerformerResponse -> false
        else -> true
    }
}
