package jp.careapp.counseling.android.ui.blocked

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.databinding.ItemBlockedBinding
import jp.careapp.counseling.android.ui.favourite.EventFavoriteAction
import jp.careapp.counseling.android.ui.favourite.FavoriteDiffCallBack

class BlockedAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction,
) : ListAdapter<FavoriteResponse, BlockedViewHolder>(
    FavoriteDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedViewHolder {
        return BlockedViewHolder.from(
            parent,
            lifecycleOwner = lifecycleOwner,
            eventsAction = events
        )
    }

    override fun onBindViewHolder(holder: BlockedViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
    override fun submitList(list: MutableList<FavoriteResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class BlockedViewHolder(
    private val binding: ItemBlockedBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FavoriteResponse) {
        binding.apply {
            blocked = item
            event = events
            lifecycleOwner = this@BlockedViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            eventsAction: EventFavoriteAction
        ): BlockedViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemBlockedBinding =
                ItemBlockedBinding.inflate(layoutInflater, parent, false)
            return BlockedViewHolder(
                binding,
                lifecycleOwner,
                eventsAction
            )
        }
    }
}
