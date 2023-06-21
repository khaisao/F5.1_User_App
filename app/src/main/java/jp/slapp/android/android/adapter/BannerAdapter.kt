package jp.slapp.android.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.slapp.android.android.data.network.BannerResponse
import jp.slapp.android.databinding.ItemBannerBinding

class BannerAdapter(private val onItemClick: (banner: BannerResponse) -> Unit) :
    ListAdapter<BannerResponse, BannerAdapter.BannerViewHolder>(BannerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        if (currentList.isNotEmpty()) {
            holder.bind(currentList[position])
        }
    }

    inner class BannerViewHolder(
        private val binding: ItemBannerBinding,
        private val onItemClick: (banner: BannerResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: BannerResponse) {
            binding.apply {
                ivBanner.loadImage(banner.bannerUrl)
                itemView.setOnClickListener {
                    onItemClick.invoke(banner)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class BannerDiffCallback : DiffUtil.ItemCallback<BannerResponse>() {
    override fun areItemsTheSame(oldItem: BannerResponse, newItem: BannerResponse): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BannerResponse, newItem: BannerResponse): Boolean {
        return oldItem == newItem
    }
}