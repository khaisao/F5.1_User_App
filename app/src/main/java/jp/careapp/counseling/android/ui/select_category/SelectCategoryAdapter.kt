package jp.careapp.counseling.android.ui.select_category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.SearchCategoryItem
import jp.careapp.counseling.databinding.ItemChooseTroubleBinding

class SelectCategoryAdapter constructor(
    context: Context,
    val onClickListener: (Int, Boolean) -> Unit
) : ListAdapter<SearchCategoryItem, SelectCategoryAdapter.SelectCategoryHolder>((SearchCategoryDiffUtil())) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoryHolder {
        return SelectCategoryHolder(
            ItemChooseTroubleBinding.inflate(layoutInflater, parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: SelectCategoryHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun submitList(list: List<SearchCategoryItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).name.hashCode().toLong()
    }

    inner class SelectCategoryHolder(
        val binding: ItemChooseTroubleBinding,
        private val onClickListener: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            data: SearchCategoryItem
        ) {
            binding.cbItem.isChecked = data.isSelected
            binding.tvName.text = data.name
            binding.viewItem.setOnClickListener {
                binding.cbItem.isChecked = !binding.cbItem.isChecked
                onClickListener.invoke(adapterPosition, binding.cbItem.isChecked)
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