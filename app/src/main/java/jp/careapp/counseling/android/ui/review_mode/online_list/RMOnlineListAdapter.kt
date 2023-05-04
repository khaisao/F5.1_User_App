package jp.careapp.counseling.android.ui.review_mode.online_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.databinding.ItemRmOnlineListBinding

class RMPerformerAdapter(private val onClickUser: (Int) -> Unit) :
    BaseAdapterLoadMore<RMPerformerResponse>(RMOnlineListDiffUtil()) {

    class RMOnlineListViewHolder(
        private val binding: ItemRmOnlineListBinding,
        private val onClickUser: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClickUser.invoke(absoluteAdapterPosition) }
        }

        fun bind(user: RMPerformerResponse) {
            binding.user = user
            if (user.thumbnailImageUrl != null) {
                Glide.with(binding.image).load(user.thumbnailImageUrl)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(binding.image.resources.getDimensionPixelSize(R.dimen._20sdp))
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            } else {
                Glide.with(binding.image).load(R.drawable.ic_no_image)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(binding.image.resources.getDimensionPixelSize(R.dimen._20sdp))
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            }
            binding.executePendingBindings()
        }
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? RMOnlineListViewHolder)?.bind(getItem(position))
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return RMOnlineListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_rm_online_list,
                parent,
                false
            ), onClickUser
        )
    }

    override fun submitList(list: MutableList<RMPerformerResponse>?) {
        val result = arrayListOf<RMPerformerResponse>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }
}

class RMOnlineListDiffUtil : DiffUtil.ItemCallback<RMPerformerResponse>() {
    override fun areItemsTheSame(
        oldItem: RMPerformerResponse,
        newItem: RMPerformerResponse
    ): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(
        oldItem: RMPerformerResponse,
        newItem: RMPerformerResponse
    ): Boolean {
        return oldItem == newItem
    }
}