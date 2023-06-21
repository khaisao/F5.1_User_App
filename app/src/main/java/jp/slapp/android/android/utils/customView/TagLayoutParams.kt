package jp.slapp.android.android.utils.customView

import android.view.ViewGroup
import android.widget.LinearLayout

class TagLayoutParams(
    val horizontalSpacing: Int,
    val verticalSpacing: Int
) : LinearLayout.LayoutParams(
    ViewGroup.LayoutParams.WRAP_CONTENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
)
