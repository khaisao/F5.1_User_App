package jp.slapp.android.android.ui.blocked

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.slapp.android.R
import jp.slapp.android.android.data.network.FavoriteResponse
import jp.slapp.android.databinding.ItemBlockedBinding
import jp.slapp.android.android.ui.favourite.FavoriteDiffCallBack

class BlockedAdapter(private val onClickBlock: (Int) -> Unit) :
    ListAdapter<FavoriteResponse, BlockedAdapter.BlockedViewHolder>(FavoriteDiffCallBack) {

    class BlockedViewHolder(private val binding: ItemBlockedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteResponse, onClickBlock: (Int) -> Unit) {
            binding.blocked = item
            binding.executePendingBindings()
            Glide.with(binding.ivPerson).load(item.thumbnailImageUrl).placeholder(R.drawable.default_avt_performer).into(binding.ivPerson)
            binding.tvBlocked.setOnClickListener { onClickBlock.invoke(absoluteAdapterPosition) }
        }
    }

    override fun submitList(list: List<FavoriteResponse>?) {
        val result = mutableListOf<FavoriteResponse>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedViewHolder {
        return BlockedViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_blocked,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BlockedViewHolder, position: Int) {
        holder.bind(getItem(position), onClickBlock)
    }
}
