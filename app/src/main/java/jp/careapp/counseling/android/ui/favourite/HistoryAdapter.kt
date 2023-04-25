package jp.careapp.counseling.android.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.core.utils.getDurationBreakdown
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.HistoryResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.ItemFavouriteBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onItemClick: (item: HistoryResponse) -> Unit,
    val context: Context
) : ListAdapter<HistoryResponse, HistoryAdapter.HistoryViewHolder>(
    HistoryDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<HistoryResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HistoryViewHolder(
        val binding: ItemFavouriteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(consultant: HistoryResponse) {
            binding.tvName.text = consultant.name
            Glide.with(context).load(consultant.thumbnailImageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.default_avt_performer)
                )
                .into(binding.ivPerson)

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

            binding.clMain.setOnClickListener {
                onItemClick(consultant)
            }

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.text = statusText

            binding.tvStatus.setBackgroundResource(statusBg)

            val bustSize = context.getBustSize(consultant.bust)
            if (bustSize == "") {
                binding.tvSize.visibility = View.GONE
            } else {
                binding.tvSize.visibility = View.VISIBLE
                binding.tvSize.text = bustSize
            }

            binding.tvAge.text = consultant.age.toString() + context.resources.getString(R.string.age_raw)

            binding.tvBody.text = consultant.messageOfTheDay
        }
    }
}

object HistoryDiffCallBack : DiffUtil.ItemCallback<HistoryResponse>() {
    override fun areItemsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean =
        oldItem == newItem
}
