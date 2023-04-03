package jp.careapp.counseling.android.ui.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.ItemBuyPointBinding
import jp.careapp.counseling.android.model.buy_point.ItemPoint

class CostPointAdapter(private val onClickItem: (ItemPoint) -> Unit) :
    ListAdapter<ItemPoint, CostPointAdapter.CostPointViewHolder>(MyPageDiffCallBack) {

    class CostPointViewHolder(private val binding: ItemBuyPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemPoint, onClickItem: (ItemPoint) -> Unit) {
            binding.data = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { onClickItem.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostPointViewHolder {
        return CostPointViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_point,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CostPointViewHolder, position: Int) {
        holder.bind(getItem(position), onClickItem)
    }
}

object MyPageDiffCallBack : DiffUtil.ItemCallback<ItemPoint>() {
    override fun areItemsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem.costFirst == newItem.costFirst

    override fun areContentsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem == newItem
}
