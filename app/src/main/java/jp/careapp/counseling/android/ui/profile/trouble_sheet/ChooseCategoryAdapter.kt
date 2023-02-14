package jp.careapp.counseling.android.ui.profile.trouble_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.SearchCategoryItem
import jp.careapp.counseling.databinding.ItemChooseTroubleBinding

class ChooseCategoryAdapter constructor(
    context: Context,
    val onClickListener: (Int, Boolean) -> Unit
) : ListAdapter<SearchCategoryItem, ChooseCategoryAdapter.SearchCategoryHolder>((SearchCategoryDiffUtil())) {
    private var itemSelected = -1
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryHolder {
        return SearchCategoryHolder(
            ItemChooseTroubleBinding.inflate(layoutInflater, parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: SearchCategoryHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun submitList(list: List<SearchCategoryItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).name.hashCode().toLong()
    }

    fun setPositionSelect(position: Int) {
        itemSelected = position
    }

    inner class SearchCategoryHolder(
        val binding: ItemChooseTroubleBinding,
        private val onClickListener: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            data: SearchCategoryItem
        ) {
            binding.tvName.text = data.name
            binding.cbItem.isChecked = itemSelected == adapterPosition
            binding.viewItem.setOnClickListener {
                onClickListener.invoke(adapterPosition, binding.cbItem.isChecked)
                itemSelected = adapterPosition
            }
        }
    }
}

class SearchCategoryDiffUtil : DiffUtil.ItemCallback<SearchCategoryItem>() {
    override fun areItemsTheSame(
        oldItem: SearchCategoryItem,
        newItem: SearchCategoryItem
    ): Boolean {
        return oldItem.name == newItem.name && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(
        oldItem: SearchCategoryItem,
        newItem: SearchCategoryItem
    ): Boolean {
        return oldItem == newItem
    }
}
