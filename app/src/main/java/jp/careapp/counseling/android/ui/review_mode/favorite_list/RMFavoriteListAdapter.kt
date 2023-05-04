package jp.careapp.counseling.android.ui.review_mode.favorite_list

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
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.databinding.ItemRmFavoriteListBinding

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
            if (user.thumbnailImageUrl != null) {
                Glide.with(binding.image).load(user.thumbnailImageUrl)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(binding.image.resources.getDimensionPixelSize(R.dimen.margin_20))
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            } else {
                Glide.with(binding.image).load(R.drawable.ic_no_image)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(binding.image.resources.getDimensionPixelSize(R.dimen.margin_20))
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)
            }
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