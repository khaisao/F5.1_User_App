package jp.slapp.android.android.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class OutlineTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    public override fun onSetAlpha(alpha: Int): Boolean {
        setTextColor(textColors.withAlpha(alpha))
        setHintTextColor(hintTextColors.withAlpha(alpha))
        setLinkTextColor(linkTextColors.withAlpha(alpha))
        return true
    }
}