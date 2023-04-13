package jp.careapp.core.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import jp.careapp.core.R

object ViewUtils {
    // check double click
    @kotlin.jvm.JvmStatic
    fun runLayoutAnimation(recyclerView: RecyclerView, @AnimRes resId: Int) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, resId)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun TextView.disableCopyPaste() {
    isLongClickable = false
    setTextIsSelectable(false)
    customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}

fun ImageView.enableView(isEnable: Boolean) {
    isEnabled = if (isEnable) {
        setColorFilter(context.getColorCompat(R.color.color_button_common_blue))
        true
    } else {
        setColorFilter(context.getColorCompat(R.color.background_color_gray))
        false
    }
}

fun ImageView.tint(@ColorRes colorId: Int) {
    setColorFilter(context.getColorCompat(colorId))
}

fun EditText.onTextChange(content: (Editable?) -> Unit) {
    addTextChangedListener(
        object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
// do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
// do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                content(s)
            }
        }
    )
}

fun ViewPager.onPageSelected(params: (Int) -> Unit) {
    addOnPageChangeListener(
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
// do nothing
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
// do nothing
            }

            override fun onPageSelected(position: Int) {
                params(position)
            }
        }
    )
}

fun View.setOnClickAction(listener: View.OnClickListener) {
}

/*fun TextView.setTextAsync(data: String) {
    TextViewCompat.setPrecomputedText(
        this,
        PrecomputedTextCompat.create(data, TextViewCompat.getTextMetricsParams(this))
    )
}*/

fun Activity.toastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun View.setMargins(start: Int, top: Int, end: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        (this.layoutParams as ViewGroup.MarginLayoutParams).setMargins(start, top, end, bottom)
        this.requestLayout()
    }
}

fun Context.convertSourceToPixel(dimenSource: Int): Int {
    return this.resources.getDimension(dimenSource).toInt()
}

fun TextView.setUnderlineAndClick(
    resourceString: Int,
    resourceColor: Int,
    start: Int,
    end: Int,
    onClick: () -> Unit
) {
    val span = SpannableString(context.getString(resourceString))
    span.setSpan(object : ClickableSpan() {
        override fun onClick(p0: View) {
            onClick.invoke()
        }
    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    span.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, resourceColor)),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = span
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setUnderlineAndClick(
    resourceString: String,
    resourceColor: Int,
    start: Int,
    end: Int,
    onClick: () -> Unit
) {
    val span = SpannableString(resourceString)
    span.setSpan(object : ClickableSpan() {
        override fun onClick(p0: View) {
            onClick.invoke()
        }
    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    span.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, resourceColor)),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = span
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun Context.convertStringToSpannableString(
    resourceString: Int,
    resourceColor: Int,
    start: Int,
    end: Int
): SpannableString {
    val span = SpannableString(this.getString(resourceString))
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this, resourceColor)),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return span
}

fun View.getHeight(function: (Int) -> Unit) {
    if (height == 0)
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                function.invoke(height)
            }
        })
    else function(height)
}