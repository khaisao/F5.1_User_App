package jp.slapp.android.android.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.setUnderlineAndClick
import jp.slapp.android.R
import jp.slapp.android.databinding.LlItemContentFaqBinding
import jp.slapp.android.databinding.LlItemHeaderFaqBinding

private const val itemHeader = 1
private const val itemContent = 2

class FAQListAdapter(private val onClickItemContent: (Int) -> Unit) :
    ListAdapter<FAQModelRecyclerView, RecyclerView.ViewHolder>(FAQlDiffUtil()) {

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
                    ), onClickItemContent
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

    class FAQContentViewHolder(
        private val binding: LlItemContentFaqBinding,
        onClickItemContent: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClickItemContent.invoke(absoluteAdapterPosition) }
        }

        fun bind(item: FAQModelRecyclerView.ItemContent) {
            binding.item = item
            binding.tvContent.setUnderlineAndClick(
                item.content,
                R.color.color_B47AFF,
                item.startSpan,
                item.endSpan,
                item.onClick
            )
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