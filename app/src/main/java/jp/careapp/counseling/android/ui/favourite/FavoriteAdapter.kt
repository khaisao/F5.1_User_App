package jp.careapp.counseling.android.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.getDurationBreakdown
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.ItemFavouriteBinding
import java.text.SimpleDateFormat
import java.util.*

class FavoriteAdapter(
    private val context: Context,
    private val listener: (Int, List<FavoriteResponse>) -> Unit,
    ) : ListAdapter<FavoriteResponse, FavoriteAdapter.FavoriteViewHolder>(
    FavoriteDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<FavoriteResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavouriteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(consultant: FavoriteResponse) {

            binding.tvName.text = consultant.name

            binding.ivPerson.loadImage(
                consultant.thumbnailImageUrl,
                R.drawable.default_avt_performer
            )

            val dateString = consultant.lastLoginDate

            if (dateString != "" && dateString != null) {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val date = sdf.parse(dateString)
                    val startDate = date?.time
                    if (startDate != null) {
                        binding.tvTime.text =
                            context.getDurationBreakdown(System.currentTimeMillis() - startDate)
                    }
                } catch (e: Exception) {
                    binding.tvTime.text = ""
                }
            } else {
                binding.tvTime.text = ""
            }

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.setBackgroundResource(statusBg)
            binding.tvStatus.text = statusText

            val bustSize = context.getBustSize(consultant.bust)
            if (bustSize == "") {
                binding.tvSize.visibility = View.GONE
            } else {
                binding.tvSize.visibility = View.VISIBLE
                binding.tvSize.text = bustSize
            }

            binding.tvAge.text = consultant.age.toString() + context.resources.getString(R.string.age_raw)

            binding.clMain.setOnClickListener {
                listener.invoke(absoluteAdapterPosition, currentList)
            }

            binding.tvBody.text = consultant.messageOfTheDay
        }
    }
}

object FavoriteDiffCallBack : DiffUtil.ItemCallback<FavoriteResponse>() {
    override fun areItemsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean =
        oldItem == newItem
}
