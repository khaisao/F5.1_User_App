package jp.slapp.android.android.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.data.network.HistoryResponse
import jp.slapp.android.android.utils.extensions.getBustSize
import jp.slapp.android.android.utils.performer_extension.PerformerStatusHandler
import jp.slapp.android.databinding.ItemFavouriteBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    val context: Context,
    private val listener: (Int, List<HistoryResponse>) -> Unit,
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

            binding.ivPerson.loadImage(
                consultant.thumbnailImageUrl,
                R.drawable.default_avt_performer
            )

            val dateString = consultant.lastLoginDate
            if (dateString != "" && dateString != null) {
                try {
                    val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val date = sdfInput.parse(dateString)
                    val sdfOutput = SimpleDateFormat("MM/dd HH:mm", Locale.US)
                    binding.tvTime.text = date?.let { sdfOutput.format(it) }
                } catch (e: Exception) {
                    binding.tvTime.text = ""
                }
            } else {
                binding.tvTime.text = ""
            }

            binding.clMain.setOnClickListener {
                listener.invoke(absoluteAdapterPosition, currentList)
            }

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.text = statusText

            binding.tvStatus.setBackgroundResource(statusBg)

            binding.tvSize.text = context.getBustSize(consultant.bust)

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
