package jp.careapp.counseling.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.GalleryResponse
import jp.careapp.counseling.databinding.ItemGalleryBinding

class GalleryAdapter(
    val context: Context,
    val screenWidth:Int,
    val onClickListener: (GalleryResponse) -> Unit
) :
    ListAdapter<GalleryResponse, GalleryAdapter.GalleryHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<GalleryResponse>() {
        override fun areItemsTheSame(
            oldItem: GalleryResponse,
            newItem: GalleryResponse
        ): Boolean {
            return oldItem.image == newItem.image && oldItem.thumbnailImage == newItem.thumbnailImage
        }

        override fun areContentsTheSame(
            oldItem: GalleryResponse,
            newItem: GalleryResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class GalleryHolder(
        val binding: ItemGalleryBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryResponse) {
            itemView.layoutParams.height = screenWidth / 3
            binding.ivMain.loadImage(item.thumbnailImage?.url, R.drawable.default_avt_performer)
            binding.ivMain.setOnClickListener {
                onClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGalleryBinding.inflate(inflater, parent, false)
        return GalleryHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
