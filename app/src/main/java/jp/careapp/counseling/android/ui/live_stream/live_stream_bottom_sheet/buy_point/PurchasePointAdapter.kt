package jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.CreditItem
import jp.careapp.counseling.databinding.ItemLivestreamBuyPointBinding

class PurchasePointAdapter(private val onClickItem: (CreditItem) -> Unit) :
    ListAdapter<CreditItem, PurchasePointAdapter.PurchasePointViewHolder>(MyPageDiffCallBack) {

    class PurchasePointViewHolder(private val binding: ItemLivestreamBuyPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreditItem, onClickItem: (CreditItem) -> Unit) {
            binding.data = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { onClickItem.invoke(item) }
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
