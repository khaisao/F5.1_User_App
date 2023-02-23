package jp.careapp.counseling.android.ui.review_mode.online_list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.model.network.BasePerformerResponse
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.databinding.ItemRmOnlineListBinding

class RMPerformerAdapter(
    context: Context,
    val onClickListener: (BasePerformerResponse) -> Unit,
    val onClickDelete: (BasePerformerResponse) -> Unit
) : BaseAdapterLoadMore<BasePerformerResponse>(RMPerformerDiffUtil()) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ItemViewHolder(ItemRmOnlineListBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemRmOnlineListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(performerResponse: BasePerformerResponse) {
            binding.performerResponse = performerResponse
            binding.root.setOnClickListener {
                onClickListener(performerResponse)
            }
            binding.btnDelete.setOnClickListener {
                onClickDelete(performerResponse)
            }
            binding.executePendingBindings()
        }
    }
}

class RMPerformerDiffUtil : DiffUtil.ItemCallback<BasePerformerResponse>() {
    override fun areItemsTheSame(
        oldItem: BasePerformerResponse,
        newItem: BasePerformerResponse
    ): Boolean {
        return when {
            oldItem is RMPerformerResponse && newItem is RMPerformerResponse -> {
                oldItem.code == newItem.code
            }
            oldItem is RMBlockListResponse && newItem is RMBlockListResponse -> {
                oldItem.code == newItem.code
            }
            oldItem is RMFavoriteResponse && newItem is RMFavoriteResponse -> {
                oldItem.code == newItem.code
            }
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: BasePerformerResponse,
        newItem: BasePerformerResponse
    ): Boolean {
        return oldItem == newItem
    }
}