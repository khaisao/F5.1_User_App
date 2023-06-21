package jp.slapp.android.android.utils.event

import androidx.lifecycle.Observer

class Event<out T>(private val content: T) {

    var hasBeenHandled: Boolean = false
        private set

    fun getContentInfoHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

class EventObserver<T>(private val observerContentInfoHandled: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>?) {
        event?.getContentInfoHandled()?.let {
            observerContentInfoHandled(it)
        }
    }
}
