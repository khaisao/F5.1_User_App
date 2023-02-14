package jp.careapp.counseling.android.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.databinding.ItemFavouriteBinding

class FavoriteAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction,
) : ListAdapter<FavoriteResponse, FavoriteViewHolder>(
    FavoriteDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.from(
            parent,
            lifecycleOwner = lifecycleOwner,
            eventsAction = events
        )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<FavoriteResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class FavoriteViewHolder(
    private val binding: ItemFavouriteBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FavoriteResponse) {
        binding.apply {
            favorite = item
            event = events
            lifecycleOwner = this@FavoriteViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            eventsAction: EventFavoriteAction
        ): FavoriteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavouriteBinding =
                ItemFavouriteBinding.inflate(layoutInflater, parent, false)
            return FavoriteViewHolder(
                binding,
                lifecycleOwner,
                eventsAction
            )
        }
    }
}

object FavoriteDiffCallBack : DiffUtil.ItemCallback<FavoriteResponse>() {
    override fun areItemsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean =
        oldItem == newItem
}
