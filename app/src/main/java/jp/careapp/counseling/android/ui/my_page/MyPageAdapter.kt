package jp.careapp.counseling.android.ui.my_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.ItemNmMyPageFieldBinding
import jp.careapp.counseling.databinding.ItemNmMyPageSpaceBinding

private const val itemField = 1
private const val itemSpace = 2

class MyPageAdapter(private val onClickItemMenu: (Int) -> Unit) :
    ListAdapter<NMMenuItem, RecyclerView.ViewHolder>(MyPageDiffUtil()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is NMMenuItem.NMMenuItemField -> itemField
        is NMMenuItem.NMMenuItemSpace -> itemSpace
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemField -> {
                MyPageFiledViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_nm_my_page_field,
                        parent,
                        false
                    )
                )
            }

            itemSpace -> {
                MyPageSpaceViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_nm_my_page_space,
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
            is NMMenuItem.NMMenuItemField -> {
                val myViewHolder = holder as MyPageFiledViewHolder
                myViewHolder.bind(item)
            }

            else -> {}
        }
    }

    override fun submitList(list: MutableList<NMMenuItem>?) {
        val result = arrayListOf<NMMenuItem>()
        list?.forEach {
            when (it) {
                is NMMenuItem.NMMenuItemField -> result.add(it.copy())
                else -> result.add(it)
            }
        }
        super.submitList(result)
    }

    inner class MyPageFiledViewHolder(private val binding: ItemNmMyPageFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClickItemMenu.invoke(absoluteAdapterPosition) }
        }

        fun bind(item: NMMenuItem.NMMenuItemField) {
            binding.menu = item
            binding.ivMenuItemField.loadImage(item.resourceImage)
            binding.executePendingBindings()
        }
    }

    class MyPageSpaceViewHolder(private val binding: ItemNmMyPageSpaceBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class MyPageDiffUtil : DiffUtil.ItemCallback<NMMenuItem>() {
    override fun areItemsTheSame(oldItem: NMMenuItem, newItem: NMMenuItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: NMMenuItem, newItem: NMMenuItem): Boolean =
        oldItem == newItem
}