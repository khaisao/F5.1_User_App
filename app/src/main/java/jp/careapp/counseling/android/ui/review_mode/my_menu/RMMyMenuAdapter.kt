package jp.careapp.counseling.android.ui.review_mode.my_menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.MenuItem
import jp.careapp.counseling.android.utils.MenuItemType
import jp.careapp.counseling.databinding.ItemFieldBinding
import jp.careapp.counseling.databinding.ItemTitleBinding

class RMMyMenuAdapter(
    context: Context,
    val callback: (MenuItem) -> Unit
) : ListAdapter<MenuItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].isTitle) {
            MenuItemType.TITLE
        } else {
            MenuItemType.FIELD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MenuItemType.TITLE -> ItemTitleViewMolder(
                ItemTitleBinding.inflate(layoutInflater, parent, false)
            )
            else -> ItemFieldViewHolder(
                ItemFieldBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemTitleViewMolder -> holder.bindData(getItem(position))
            is ItemFieldViewHolder -> holder.bindData(getItem(position))
        }
    }

    inner class ItemTitleViewMolder(val binding: ItemTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(menu: MenuItem) {
            binding.menu = menu
            binding.executePendingBindings()
        }
    }

    inner class ItemFieldViewHolder(val binding: ItemFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(menu: MenuItem) {
            binding.menu = menu
            binding.rootLayout.setOnClickListener {
                callback(menu)
            }
            binding.executePendingBindings()
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
        return oldItem == newItem
    }
}