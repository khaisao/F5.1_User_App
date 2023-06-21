package jp.slapp.android.android.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.executeAfter
import jp.slapp.android.android.data.network.NewsResponse
import jp.slapp.android.databinding.ItemNewsBinding

class NewsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: NewsEvent
) : ListAdapter<NewsResponse, NewsViewholder>(NewsDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewholder {
        return NewsViewholder.from(parent, lifecycleOwner = lifecycleOwner, events = events)
    }

    override fun onBindViewHolder(holder: NewsViewholder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }
}

class NewsViewholder(
    private val binding: ItemNewsBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val events: NewsEvent
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: NewsResponse) {
        var check = false
        binding.executeAfter {
            news = item
            newsItem.setOnClickListener {
                check = !check
                if (check) {
                    binding.ivIsOpen.rotation = 90.toFloat()
                    binding.tvContent.visibility = View.VISIBLE
                } else {
                    binding.ivIsOpen.rotation = 270.toFloat()
                    binding.tvContent.visibility = View.GONE
                }
                events.newsClick(item)
            }
            lifecycleOwner = this@NewsViewholder.lifecycleOwner
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            events: NewsEvent
        ): NewsViewholder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemNewsBinding = ItemNewsBinding.inflate(layoutInflater, parent, false)
            return NewsViewholder(binding, lifecycleOwner, events)
        }
    }
}

object NewsDiffCallBack : DiffUtil.ItemCallback<NewsResponse>() {
    override fun areItemsTheSame(oldItem: NewsResponse, newItem: NewsResponse): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NewsResponse, newItem: NewsResponse): Boolean =
        oldItem == newItem
}
