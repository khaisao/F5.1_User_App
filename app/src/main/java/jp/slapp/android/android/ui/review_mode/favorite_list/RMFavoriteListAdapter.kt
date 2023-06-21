package jp.slapp.android.android.ui.review_mode.favorite_list

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
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.model.network.RMFavoriteResponse
import jp.slapp.android.databinding.ItemRmFavoriteListBinding

class RMFavoriteListAdapter(
    private val onClickUser: (Int) -> Unit,
    private val onClickDelete: (Int) -> Unit
) : ListAdapter<RMFavoriteResponse, RMFavoriteListAdapter.RMFavoriteListViewHolder>(
    RMFavoriteListDiffUtil()
) {

    class RMFavoriteListViewHolder(
        private val binding: ItemRmFavoriteListBinding,
        private val onClickUser: (Int) -> Unit,
        private val onClickDelete: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClickUser.invoke(absoluteAdapterPosition) }
            binding.btnDelete.setOnClickListener { onClickDelete.invoke(absoluteAdapterPosition) }
        }

        fun bind(user: RMFavoriteResponse) {
            binding.user = user
            binding.image.loadImage(user.thumbnailImageUrl, R.drawable.ic_no_image)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RMFavoriteListViewHolder {
        return RMFavoriteListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_rm_favorite_list,
                parent,
                false
            ), onClickUser, onClickDelete
        )
    }

    override fun onBindViewHolder(holder: RMFavoriteListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RMFavoriteResponse>?) {
        val result = arrayListOf<RMFavoriteResponse>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }
}

class RMFavoriteListDiffUtil : DiffUtil.ItemCallback<RMFavoriteResponse>() {
    override fun areItemsTheSame(
        oldItem: RMFavoriteResponse,
        newItem: RMFavoriteResponse
    ): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(
        oldItem: RMFavoriteResponse,
        newItem: RMFavoriteResponse
    ): Boolean {
        return oldItem == newItem
    }
}