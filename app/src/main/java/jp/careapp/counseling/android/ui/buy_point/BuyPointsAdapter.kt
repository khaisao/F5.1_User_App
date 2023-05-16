package jp.careapp.counseling.android.ui.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.buy_point.ItemPoint
import jp.careapp.counseling.databinding.ItemBuyPointsBinding

class BuyPointsAdapter(private val onClickItem: (ItemPoint) -> Unit) :
    ListAdapter<ItemPoint, BuyPointsAdapter.BuyPointsViewHolder>(BuyPointsDiffCallBack()) {

    class BuyPointsViewHolder(private val binding: ItemBuyPointsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemPoint, onClickItem: (ItemPoint) -> Unit) {
            binding.data = item
            binding.tvPointCount.onSetAlpha(255)
            binding.executePendingBindings()

            binding.root.setOnClickListener { onClickItem.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyPointsViewHolder {
        return BuyPointsViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_points,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BuyPointsViewHolder, position: Int) {
        holder.bind(getItem(position), onClickItem)
    }
}

class BuyPointsDiffCallBack : DiffUtil.ItemCallback<ItemPoint>() {
    override fun areItemsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem.itemId == newItem.itemId

    override fun areContentsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem == newItem
}