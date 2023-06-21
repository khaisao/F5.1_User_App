package jp.slapp.android.android.ui.buy_point.bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.slapp.android.R
import jp.slapp.android.android.model.buy_point.ItemPoint
import jp.slapp.android.android.ui.buy_point.BuyPointsDiffCallBack
import jp.slapp.android.databinding.ItemBuyPointsBottomSheetBinding

class BuyPointsBottomSheetAdapter(private val onClickItem: (ItemPoint) -> Unit) :
    ListAdapter<ItemPoint, BuyPointsBottomSheetAdapter.BuyPointBottomSheetViewHolder>(
        BuyPointsDiffCallBack()
    ) {

    class BuyPointBottomSheetViewHolder(private val binding: ItemBuyPointsBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemPoint, onClickItem: (ItemPoint) -> Unit) {
            binding.data = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { onClickItem.invoke(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyPointBottomSheetViewHolder {
        return BuyPointBottomSheetViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_points_bottom_sheet,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BuyPointBottomSheetViewHolder, position: Int) {
        holder.bind(getItem(position), onClickItem)
    }
}