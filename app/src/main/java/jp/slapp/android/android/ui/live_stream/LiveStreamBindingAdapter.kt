package jp.slapp.android.android.ui.live_stream

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

fun TextView.setLiveStreamCommentText(name: String, content: String, resourceColor: Int) {
    val message = "$name　$content"
    val span = SpannableString(message)
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, resourceColor)),
        0,
        name.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = span
}