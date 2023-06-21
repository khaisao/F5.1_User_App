package jp.slapp.android.android.utils.extensions

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.RealmModel
import io.realm.RealmResults
import jp.slapp.android.R
import jp.slapp.android.android.utils.realmUtil.LiveRealmData

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
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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

fun String.toPayLength() = this.replace("\n", "").replace("\r", "").trim().length

fun String.toPayPoint(pointPerChar: Int) =
    pointPerChar * (((this.toPayLength() - 1) / 10) + 1)

inline fun <reified T> Any.toListData(): List<T> {
    return if (this is List<*>) {
        val gson = Gson()
        gson.fromJson<List<T>>(
            gson.toJson(this),
            TypeToken.getParameterized(List::class.java, T::class.java).type
        ) ?: emptyList()
    } else {
        emptyList()
    }
}

fun Context.getBustSize(numSize: Int): String {
    return if (numSize in 1..9) {
        val sizeString = (numSize + 64).toChar().toString()
        sizeString + this.resources.getString(R.string.cup)
    } else {
        return this.resources.getString(R.string.secret)
    }
}