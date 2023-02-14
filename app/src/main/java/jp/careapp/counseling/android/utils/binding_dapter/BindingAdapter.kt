package jp.careapp.counseling.android.utils.binding_dapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.counseling.R

@BindingAdapter("app:goneIf")
fun View.goneIf(isGone: Boolean) {
    visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter(value = ["app:srcUri", "app:srcPlaceholder"], requireAll = false)
fun ImageView.srcGlideUri(uri: Uri?, placeholder: Drawable?) {
    when (uri) {
        null -> {
            Glide.with(this.context)
                .load(placeholder)
                .circleCrop()
                .into(this)
        }
        else -> {
            Glide.with(this.context).load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .circleCrop()
                .into(this)
        }
    }
}

@BindingAdapter(value = ["app:srcUrl", "app:srcPlaceholder"], requireAll = false)
fun ImageView.srcGlideUrl(url: String?, placeholder: Drawable?) {
    srcGlideUri(url?.toUri(), resources.getDrawable(R.drawable.ic_thumbnailnoimage))
}
