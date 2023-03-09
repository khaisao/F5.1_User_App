package jp.careapp.counseling.android.ui.review_mode.userDetail

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import jp.careapp.counseling.R

@BindingAdapter("rmPerformerStatus")
fun TextView.setStatus(rmPerformerStatus: Int) {
    if (rmPerformerStatus == 0) {
        text = context.getString(R.string.offline)
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_offline_status, 0, 0, 0)
        setTextColor(ContextCompat.getColor(context, R.color.color_c0c3c9))
    } else {
        text = context.getString(R.string.online)
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online_status, 0, 0, 0)
        setTextColor(ContextCompat.getColor(context, R.color.color_070707))
    }
}

@BindingAdapter("rmPerformerStatus")
fun LinearLayout.setStatus(rmPerformerStatus: Int) {
    background = if (rmPerformerStatus == 0)
        ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_disable)
    else
        ContextCompat.getDrawable(context, R.drawable.bg_rm_btn_chat_msg_enable)
}

@BindingAdapter("isFavorite")
fun ImageView.setFavorite(isFavorite: Boolean) {
    background = if (isFavorite)
        ContextCompat.getDrawable(context, R.drawable.ic_add_favorite_active)
    else
        ContextCompat.getDrawable(context, R.drawable.ic_add_favorite_inactive)
}