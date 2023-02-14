package jp.careapp.counseling.android.utils.extensions

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.realm.RealmModel
import io.realm.RealmResults
import jp.careapp.counseling.android.utils.realmUtil.LiveRealmData

fun <A, B, R> LiveData<A>.combine(
    orther: LiveData<B>,
    combiner: (A, B) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) { a ->
        val b = orther.value
        if (b != null) {
            result.postValue(combiner(a, b))
        }
    }
    result.addSource(orther) { b ->
        val a = this@combine.value
        if (a != null) {
            result.postValue(combiner(a, b))
        }
    }
    return result
}

fun <T : RealmModel> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)

inline fun <reified T> View.findParentOfType(): T? {
    var p = parent
    while (p != null && p !is T) p = p.parent
    return p as T?
}

/**
 * Scroll down the minimum needed amount to show [descendant] in full. More
 * precisely, reveal its bottom.
 */
fun ViewGroup.scrollDownTo(descendant: View) {
    // Could use smoothScrollBy, but it sometimes over-scrolled a lot
    howFarDownIs(descendant)?.let { scrollBy(0, it) }
}

/**
 * Calculate how many pixels below the visible portion of this [ViewGroup] is the
 * bottom of [descendant].
 *
 * In other words, how much you need to scroll down, to make [descendant]'s bottom
 * visible.
 */
fun ViewGroup.howFarDownIs(descendant: View): Int? {
    val bottom = Rect().also {
        // See https://stackoverflow.com/a/36740277/1916449
        descendant.getDrawingRect(it)
        offsetDescendantRectToMyCoords(descendant, it)
    }.bottom
    return (bottom - height - scrollY).takeIf { it > 0 }
}

fun Context.isNetworkAvailable(): Boolean {
    val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val connection = manager.activeNetworkInfo
    return connection != null && connection.isConnectedOrConnecting
}

fun ChipGroup.updateTextStyleChecked() {
    for (i in 0 until childCount) {
        val chip: Chip = getChildAt(i) as Chip
        if (chip.isChecked) {
            chip.setTypeface(chip.typeface, Typeface.BOLD)
        } else {
            chip.setTypeface(null, Typeface.NORMAL)
        }

    }
}

fun RadioGroup.updateTextStyleChecked() {
    for (i in 0 until childCount) {
        (getChildAt(i) as? RadioButton)?.let { radio ->
            if (radio.isChecked) {
                radio.setTypeface(radio.typeface, Typeface.BOLD)
            } else {
                radio.setTypeface(radio.typeface, Typeface.BOLD)
            }
        }
    }
}

fun Fragment.dismissAllDialog() {
    childFragmentManager.fragments.let { fragments ->
        if (fragments.isNotEmpty())
            for (fragment in fragments) {
                when (fragment) {
                    is DialogFragment -> fragment.dismiss()
                    is Dialog -> fragment.dismiss()
                    else -> {}
                }
            }
    }
}

fun Fragment.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

fun Int.toDurationTime(): String {
    val hours = this / 3600
    val secondsLeft = this - hours * 3600
    val minutes = secondsLeft / 60
    val seconds = secondsLeft - minutes * 60
    var formattedTime = ""
    if (hours > 0) {
        if (hours < 10) formattedTime += "0"
        formattedTime += "$hours:"
    }
    if (minutes < 10) formattedTime += "0"
    formattedTime += "$minutes:"
    if (seconds < 10) formattedTime += "0"
    formattedTime += seconds
    return formattedTime
}

fun String.toPayLength() = this.replace("\n", "").replace("\r", "").trim().length

fun String.toPayPoint(pointPerChar: Int) =
    pointPerChar * (((this.toPayLength() - 1) / 10) + 1)
