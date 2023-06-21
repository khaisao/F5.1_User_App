package jp.slapp.android.android.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapterLoadMore<D>(diff: DiffUtil.ItemCallback<D>) :
    ListAdapter<D, RecyclerView.ViewHolder>(diff) {
    var disableLoadMore = true
    var isLoading = false
    private val visibleThreshold = 1
    private var loadMorelistener: LoadMorelistener? = null
    private var dataList: MutableList<D?> = mutableListOf()
    private var recyclerView: RecyclerView? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBinViewHolderNomal(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolderNormal(parent, viewType)
    }

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    fun setDisableLoadmore(disableLoadMore: Boolean) {
        this.disableLoadMore = disableLoadMore
    }

    fun setLoadMorelistener(loadMorelistener: LoadMorelistener) {
        disableLoadMore = false
        this.loadMorelistener = loadMorelistener
    }

    fun getLoadMorelistener() = this.loadMorelistener

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            if (disableLoadMore || isLoading) {
                                return
                            }
                            var lastVisibleItemPosition = 0
                            val layoutManager =
                                recyclerView.layoutManager
                            val totalItemCount = this@BaseAdapterLoadMore.itemCount
                            if (layoutManager is LinearLayoutManager) {
                                lastVisibleItemPosition =
                                    layoutManager.findLastVisibleItemPosition()
                            }
                            if (lastVisibleItemPosition + visibleThreshold >= totalItemCount) {
                                if (loadMorelistener != null) {
                                    Log.d(
                                        "BaseAdapterLoadMore",
                                        "onScrollStateChanged() called with: recyclerView = $recyclerView, newState = $newState"
                                    )
                                    loadMorelistener?.onLoadMore()
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        )
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    interface LoadMorelistener {
        fun onLoadMore()
    }

    abstract fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int)
    abstract fun onCreateViewHolderNormal(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
}
