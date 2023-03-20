package jp.careapp.counseling.android.ui.faq

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.NMTypeField

@BindingAdapter("gone_collapse")
fun TextView.goneCollapse(isCollapse: Boolean) {
    visibility = if (isCollapse) TextView.GONE else TextView.VISIBLE
}

@BindingAdapter("src_collapse")
fun ImageView.setSrcCollapse(isCollapse: Boolean) {
    if (isCollapse) {
        Glide.with(this.context)
            .load(R.drawable.ic_expand_faq)
            .into(this)
    } else {
        Glide.with(this.context)
            .load(R.drawable.ic_collapse_faq)
            .into(this)
    }
}

@BindingAdapter("load_image")
fun ImageView.loadImage(src: Int) {
    Glide.with(this.context).load(src).into(this)
}

@BindingAdapter("show_divider")
fun View.showDivider(typeField: NMTypeField) {
    isVisible = when (typeField) {
        NMTypeField.TOP, NMTypeField.CENTER -> true
        else -> false
    }
}

@BindingAdapter("bg_field")
fun View.setBackgroundField(typeField: NMTypeField) {
    when (typeField) {
        NMTypeField.TOP -> setBackgroundResource(R.drawable.bg_nm_top_field)
        NMTypeField.CENTER -> setBackgroundResource(R.drawable.bg_nm_none_field)
        NMTypeField.BOTTOM -> setBackgroundResource(R.drawable.bg_nm_bottom_field)
        NMTypeField.ONLY -> setBackgroundResource(R.drawable.bg_only_field)
    }
}