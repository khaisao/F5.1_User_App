package jp.careapp.counseling.android.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.LlItemContentFaqBinding
import jp.careapp.counseling.databinding.LlItemHeaderFaqBinding

private const val itemHeader = 1
private const val itemContent = 2

class FAQListAdapter : ListAdapter<FAQModelRecyclerView, RecyclerView.ViewHolder>(FAQlDiffUtil()) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is FAQModelRecyclerView.ItemHeader -> itemHeader
        is FAQModelRecyclerView.ItemContent -> itemContent
    }

    override fun submitList(list: MutableList<FAQModelRecyclerView>?) {
        val result = arrayListOf<FAQModelRecyclerView>()
        list?.forEach {
            when (it) {
                is FAQModelRecyclerView.ItemHeader -> result.add(it.copy())
                is FAQModelRecyclerView.ItemContent -> result.add(it.copy())
                else -> result.add(it)
            }
        }
        super.submitList(result)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemHeader -> {
                FAQHeaderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.ll_item_header_faq,
                        parent,
                        false
                    )
                )
            }
            itemContent -> {
                FAQContentViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.ll_item_content_faq,
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FAQModelRecyclerView.ItemHeader -> {
                val myViewHolder = holder as FAQHeaderViewHolder
                myViewHolder.bind(item)
            }
            is FAQModelRecyclerView.ItemContent -> {
                val myViewHolder = holder as FAQContentViewHolder
                myViewHolder.bind(item)
            }
            else -> {}
        }
    }

    class FAQHeaderViewHolder(private val binding: LlItemHeaderFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQModelRecyclerView.ItemHeader) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class FAQContentViewHolder(private val binding: LlItemContentFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQModelRecyclerView.ItemContent) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}

class FAQlDiffUtil : DiffUtil.ItemCallback<FAQModelRecyclerView>() {
    override fun areItemsTheSame(
        oldItem: FAQModelRecyclerView,
        newItem: FAQModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FAQModelRecyclerView,
        newItem: FAQModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }
}