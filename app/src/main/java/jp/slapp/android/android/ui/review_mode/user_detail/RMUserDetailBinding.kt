package jp.slapp.android.android.ui.review_mode.user_detail

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import jp.slapp.android.R
import jp.slapp.android.android.utils.RMCallStatus
import jp.slapp.android.android.utils.RMPresenceStatus

@BindingAdapter("rmPerformerStatus")
fun TextView.setStatus(presenceStatus: Int) {
    when (presenceStatus) {
        RMPresenceStatus.AWAY -> {
            text = context.getString(R.string.offline)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_offline_status, 0, 0, 0)
            setTextColor(ContextCompat.getColor(context, R.color.color_c0c3c9))
        }
        RMPresenceStatus.ACCEPTING -> {
            text = context.getString(R.string.online)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online_status, 0, 0, 0)
            setTextColor(ContextCompat.getColor(context, R.color.color_070707))
        }
    }
}

@BindingAdapter("chatButtonStatus")
fun LinearLayout.setChatButtonStatus(presenceStatus: Int) {
    when (presenceStatus) {
        RMPresenceStatus.AWAY -> {
            background = ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_disable)
            isEnabled = false
        }
        RMPresenceStatus.ACCEPTING -> {
            background = ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_enable)
            isEnabled = true
        }
    }
}

@BindingAdapter("callButtonStatus")
fun LinearLayout.setCallButtonStatus(callStatus: Int) {
    when (callStatus) {
        RMCallStatus.OFFLINE -> {
            background = ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_disable)
            isEnabled = false
        }
        RMCallStatus.WAITING_OFFLINE, RMCallStatus.INCOMING_CALL -> {
            background = ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_enable)
            isEnabled = true
        }
    }
}

@BindingAdapter("isFavorite")
fun ImageView.setFavorite(isFavorite: Boolean) {
    background = if (isFavorite)
        ContextCompat.getDrawable(context, R.drawable.ic_add_favorite_active)
    else
        ContextCompat.getDrawable(context, R.drawable.ic_add_favorite_inactive)
}