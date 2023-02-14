package jp.careapp.core.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.databinding.ViewDataBinding
import java.util.*

@ColorInt
fun Context.getColorCompat(@ColorRes resourceId: Int) = ContextCompat.getColor(this, resourceId)

fun Context.getDrawableCompat(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)

fun ImageView.setDrawableCompat(context: Context, @DrawableRes resId: Int) =
    setImageDrawable(context.getDrawableCompat(resId))

fun Context.getDimension(@DimenRes resourceId: Int) = resources.getDimension(resourceId)

val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.getPxFromDp(dp: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun AppCompatTextView.setTextCompute(text: CharSequence) {
    setTextFuture(PrecomputedTextCompat.getTextFuture(text, textMetricsParamsCompat, null))
}

inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

fun Context.showDatePicker(
    calendar: Calendar, listener: DatePickerDialog.OnDateSetListener, isAgeLimit: Boolean = false
) {
    DatePickerDialog(
        this,
        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
        listener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        if (isAgeLimit) {
            datePicker.maxDate = Calendar.getInstance()
                .apply { set(Calendar.YEAR, get(Calendar.YEAR) - 18) }.timeInMillis
        }
        show()
    }
}
