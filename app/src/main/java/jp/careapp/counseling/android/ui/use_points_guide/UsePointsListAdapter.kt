package jp.careapp.counseling.android.ui.use_points_guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.ItemUsePointsGuideBinding

class UsePointsListAdapter :
    ListAdapter<UsePointsModelRecyclerView, UsePointsListAdapter.UsePointsListViewHolder>(
        UsePointsListDiffUtil()
    ) {

    class UsePointsListViewHolder(private val binding: ItemUsePointsGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UsePointsModelRecyclerView) {
            binding.item = item
            binding.ivItemIcon.loadImage(item.resourceImage)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsePointsListViewHolder {
        return UsePointsListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_use_points_guide,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsePointsListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class UsePointsListDiffUtil : DiffUtil.ItemCallback<UsePointsModelRecyclerView>() {
    override fun areItemsTheSame(
        oldItem: UsePointsModelRecyclerView,
        newItem: UsePointsModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: UsePointsModelRecyclerView,
        newItem: UsePointsModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }
}