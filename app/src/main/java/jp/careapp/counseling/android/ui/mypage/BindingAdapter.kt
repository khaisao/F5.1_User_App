package jp.careapp.counseling.android.ui.mypage

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:goneIf")
fun View.goneIf(isGone: Boolean) {
    visibility = if (isGone) View.GONE else View.VISIBLE
}
