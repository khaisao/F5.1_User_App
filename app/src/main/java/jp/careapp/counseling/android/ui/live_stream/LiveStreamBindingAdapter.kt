package jp.careapp.counseling.android.ui.live_stream

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("content_visibility")
fun View.bindVisibility(content: SpannableString?) {
    visibility = if (content.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun TextView.setLiveStreamCommentText(content: String, resourceColor: Int) {
    val span = SpannableString(content)
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, resourceColor)),
        0,
        4,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = span
}