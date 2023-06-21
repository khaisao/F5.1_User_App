package jp.slapp.android.android.ui.review_mode.online_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.adapter.BaseAdapterLoadMore
import jp.slapp.android.android.model.network.RMPerformerResponse
import jp.slapp.android.databinding.ItemRmOnlineListBinding

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
            binding.image.loadImage(user.thumbnailImageUrl, R.drawable.ic_no_image)
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