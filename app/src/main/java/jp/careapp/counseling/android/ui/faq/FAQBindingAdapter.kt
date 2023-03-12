package jp.careapp.counseling.android.ui.faq

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TypeField

@BindingAdapter("goneCollapse")
fun TextView.goneCollapse(isCollapse: Boolean) {
    visibility = if (isCollapse) TextView.GONE else TextView.VISIBLE
}

@BindingAdapter("setSrcCollapse")
fun ImageView.setSrcCollapse(isCollapse: Boolean) {
    if (isCollapse) {
        Glide.with(this.context)
            .load(R.drawable.ic_collapse_faq)
            .into(this)
    } else {
        Glide.with(this.context)
            .load(R.drawable.ic_expand_faq)
            .into(this)
    }
}

@BindingAdapter("typeField", "isCollapse")
fun View.showDivider(typeField: TypeField, isCollapse: Boolean) {
    isVisible = if (isCollapse) {
        (typeField == TypeField.CENTER || typeField == TypeField.TOP)
    } else {
        false
    }
}