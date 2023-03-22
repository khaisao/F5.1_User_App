package jp.careapp.counseling.android.ui.live_stream

import android.text.SpannableString
import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("content_visibility")
fun View.bindVisibility(content: SpannableString?) {
    visibility = if (content.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}