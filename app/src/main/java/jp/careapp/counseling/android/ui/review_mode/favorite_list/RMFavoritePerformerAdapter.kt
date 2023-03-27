package jp.careapp.counseling.android.ui.review_mode.online_list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.model.network.BasePerformerResponse
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.android.ui.review_mode.block_list.RMBlockListDiffUtil
import jp.careapp.counseling.databinding.ItemRmFavoriteListBinding

class RMFavoritePerformerAdapter(
    context: Context,
    val onClickListener: (BasePerformerResponse) -> Unit,
    val onClickDelete: (BasePerformerResponse) -> Unit
) : BaseAdapterLoadMore<BasePerformerResponse>(RMBlockListDiffUtil()) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private val mContext: Context = context

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ItemViewHolder(ItemRmFavoriteListBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemRmFavoriteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(performerResponse: BasePerformerResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Glide.with(binding.image).load(
                    mContext.resources?.getIdentifier(
                        "ic_no_image",
                        "drawable", mContext.packageName
                    )
                )
                    .transform(RoundedCorners(mContext.resources.getDimensionPixelSize(R.dimen._10sdp)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            } else {
                Glide.with(binding.image).load(
                    mContext.resources?.getIdentifier(
                        "ic_no_image",
                        "drawable", mContext.packageName
                    )
                ).transforms(
                    CenterCrop(),
                    RoundedCorners(mContext.resources.getDimensionPixelSize(R.dimen._10sdp))
                )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            }
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

class RMFavoritePerformerDiffUtil : DiffUtil.ItemCallback<BasePerformerResponse>() {
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