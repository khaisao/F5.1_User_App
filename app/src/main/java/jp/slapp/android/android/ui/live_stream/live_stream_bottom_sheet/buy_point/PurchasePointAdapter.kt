package jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.slapp.android.R
import jp.slapp.android.android.data.model.CreditItem
import jp.slapp.android.android.utils.formatDecimalSeparator
import jp.slapp.android.databinding.ItemLivestreamBuyPointBinding

class PurchasePointAdapter(private val onClickItem: (CreditItem) -> Unit) :
    ListAdapter<CreditItem, PurchasePointAdapter.PurchasePointViewHolder>(MyPageDiffCallBack) {

    class PurchasePointViewHolder(private val binding: ItemLivestreamBuyPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreditItem, onClickItem: (CreditItem) -> Unit) {
            binding.data = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { onClickItem.invoke(item) }
            var totalPoint = 0
            if (item.buyPoint != null && item.bonusPoint != null) {
                totalPoint = item.buyPoint + item.bonusPoint
            }
            binding.tvPoint.text = buildString {
                append("¥")
                append((item.price?.formatDecimalSeparator() ?: ""))
                append(" / ")
                append(totalPoint.formatDecimalSeparator())
                append("pts")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasePointViewHolder {
        return PurchasePointViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_livestream_buy_point,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PurchasePointViewHolder, position: Int) {
        holder.bind(getItem(position), onClickItem)
    }
}

object MyPageDiffCallBack : DiffUtil.ItemCallback<CreditItem>() {
    override fun areItemsTheSame(oldItem: CreditItem, newItem: CreditItem): Boolean =
        oldItem.price == newItem.price

    override fun areContentsTheSame(oldItem: CreditItem, newItem: CreditItem): Boolean =
        oldItem == newItem
}
