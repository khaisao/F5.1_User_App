package jp.slapp.android.android.adapter

import androidx.annotation.LayoutRes

abstract class BaseAdapter {

    @LayoutRes
    protected abstract fun layoutId(): Int
}
