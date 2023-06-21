package jp.slapp.android.android.ui.review_mode.block_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jp.slapp.android.R
import jp.slapp.android.android.model.network.RMBlockListResponse
import jp.slapp.android.databinding.ItemRmBlockListBinding

class RMBlockListAdapter(
    private val onClickDelete: (Int) -> Unit
) : ListAdapter<RMBlockListResponse, RMBlockListAdapter.RMBlockListViewHolder>(RMBlockListDiffUtil()) {

    class RMBlockListViewHolder(
        val binding: ItemRmBlockListBinding,
        private val onClickDelete: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnDelete.setOnClickListener { onClickDelete.invoke(absoluteAdapterPosition) }
        }

        fun bind(user: RMBlockListResponse) {
            Glide.with(binding.image).load(R.drawable.ic_no_image)
                .transform(
                    CenterCrop(),
                    RoundedCorners(binding.image.resources.getDimensionPixelSize(R.dimen._10sdp))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.image)
            binding.user = user
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RMBlockListViewHolder {
        return RMBlockListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_rm_block_list,
                parent,
                false
            ), onClickDelete
        )
    }

    override fun onBindViewHolder(holder: RMBlockListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RMBlockListResponse>?) {
        val result = arrayListOf<RMBlockListResponse>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }
}

class RMBlockListDiffUtil : DiffUtil.ItemCallback<RMBlockListResponse>() {
    override fun areItemsTheSame(
        oldItem: RMBlockListResponse,
        newItem: RMBlockListResponse
    ): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(
        oldItem: RMBlockListResponse,
        newItem: RMBlockListResponse
    ): Boolean {
        return oldItem == newItem
    }
}