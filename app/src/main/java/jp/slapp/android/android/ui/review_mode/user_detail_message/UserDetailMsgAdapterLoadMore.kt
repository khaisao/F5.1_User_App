package jp.slapp.android.android.ui.review_mode.user_detail_message

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.slapp.android.android.data.model.message.RMMessageResponse

abstract class UserDetailMsgAdapterLoadMore<D>(diff: DiffUtil.ItemCallback<D>) :
    ListAdapter<D, RecyclerView.ViewHolder>(diff) {
    var disableLoadMore = true
    var isLoading = false
    private val visibleThreshold = 1
    private var loadMorelistener: LoadMorelistener? = null
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
                            var firstVisibleItemPosition = 0
                            var lastVisibleItemPosition = 0

                            val layoutManager =
                                recyclerView.layoutManager
                            if (layoutManager is LinearLayoutManager) {
                                firstVisibleItemPosition =
                                    layoutManager.findFirstVisibleItemPosition()
                                lastVisibleItemPosition =
                                    layoutManager.findLastVisibleItemPosition()
                            }
                            if (firstVisibleItemPosition < visibleThreshold && lastVisibleItemPosition < itemCount - 1) {
                                if (loadMorelistener != null) {
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

    fun checkContainMsg(mailCode: String): Boolean {
        val data = currentList.filter { it is RMMessageResponse && it.code == mailCode }
        data.forEach {
            if (it is RMMessageResponse) {
                it.open = true
            }
        }
        if (!data.isNullOrEmpty()) {
            notifyDataSetChanged()
        }
        return !data.isNullOrEmpty()
    }

    fun openPayMessage(mailCode: String) {
        val data = currentList.filter { it is RMMessageResponse && it.code == mailCode }
        data.forEach {
            if (it is RMMessageResponse) {
                it.open = true
            }
        }
        if (!data.isNullOrEmpty()) {
            notifyDataSetChanged()
        }
    }
}
