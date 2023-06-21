package jp.slapp.android.android.ui.profile.tab_review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.slapp.android.databinding.ItemReviewUserProfileBinding
import jp.slapp.android.android.model.user_profile.ReviewUserProfile

class ReviewUserAdapter(
    private val lifecycleOwner: LifecycleOwner,
) : ListAdapter<ReviewUserProfile, ReviewUserViewHolder>(
    ChatListDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewUserViewHolder {
        return ReviewUserViewHolder.from(
            parent,
            lifecycleOwner = lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: ReviewUserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class ReviewUserViewHolder(
    private val binding: ItemReviewUserProfileBinding,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ReviewUserProfile) {
        binding.apply {
            data = item
            lifecycleOwner = this@ReviewUserViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
        ): ReviewUserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemReviewUserProfileBinding =
                ItemReviewUserProfileBinding.inflate(layoutInflater, parent, false)
            return ReviewUserViewHolder(
                binding,
                lifecycleOwner,
            )
        }
    }
}

object ChatListDiffCallBack : DiffUtil.ItemCallback<ReviewUserProfile>() {
    override fun areItemsTheSame(oldItem: ReviewUserProfile, newItem: ReviewUserProfile): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ReviewUserProfile,
        newItem: ReviewUserProfile
    ): Boolean =
        oldItem == newItem
}
