package jp.careapp.counseling.android.ui.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.core.utils.getDurationBreakdown
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.HistoryResponse
import jp.careapp.counseling.android.utils.CallStatus
import jp.careapp.counseling.android.utils.ChatStatus
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.databinding.ItemFavouriteBinding
import jp.careapp.counseling.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventHistoryAction,
) : ListAdapter<HistoryResponse, HistoryViewHolder>(
    HistoryDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.from(
            parent,
            lifecycleOwner = lifecycleOwner,
            eventsAction = events
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<HistoryResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class HistoryViewHolder(
    private val binding: ItemHistoryBinding,
    private val lifecycleOwner: LifecycleOwner,
    private var events: EventHistoryAction
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HistoryResponse) {
        binding.apply {
            history = item
            events = events
            lifecycleOwner = this@HistoryViewHolder.lifecycleOwner
            executePendingBindings()
        }
        binding.tvName.text=item.name
        Glide.with(binding.root.context).load(item.thumbnailImageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.default_avt_performer)
            )
            .into(binding.ivPerson)

        //testData
        val dateString = "2023-01-20 08:57:10"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US)
        val date = sdf.parse(dateString)
        val startDate = date?.time
        if (startDate != null) {
            binding.tvTime.text =
                binding.root.context.getDurationBreakdown(System.currentTimeMillis() - startDate)
        }

        binding.clMain.setOnClickListener {
            events.onclickItem(item)
        }

        if (item.callStatus == CallStatus.ONLINE && item.chatStatus == ChatStatus.OFFLINE) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_offline)
        } else if (item.callStatus == CallStatus.INCOMING_CALL && item.chatStatus == ChatStatus.OFFLINE) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_offline)
        } else if (item.callStatus == CallStatus.OFFLINE && item.chatStatus == ChatStatus.WAITING) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_live_streaming)
        } else if (item.callStatus == CallStatus.OFFLINE && item.chatStatus == ChatStatus.CHATTING) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_live_streaming)
        } else if (item.callStatus == CallStatus.OFFLINE && item.chatStatus == ChatStatus.TWO_SHOT_CHAT) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_private_delivery)
        } else if (item.callStatus == CallStatus.OFFLINE && item.chatStatus == ChatStatus.OFFLINE) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_waiting)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_waiting)
        } else {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_offline)
        }

        val bustSize = binding.root.context.getBustSize(item.bust)
        if (bustSize == "") {
            binding.tvSize.visibility = View.GONE
        } else {
            binding.tvSize.visibility = View.VISIBLE
            binding.tvSize.text = bustSize
        }

        binding.tvAge.text = item.age.toString() + binding.root.context.resources.getString(R.string.age_raw)

    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            eventsAction: EventHistoryAction
        ): HistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemHistoryBinding =
                ItemHistoryBinding.inflate(layoutInflater, parent, false)
            return HistoryViewHolder(
                binding,
                lifecycleOwner,
                eventsAction
            )
        }
    }
}

object HistoryDiffCallBack : DiffUtil.ItemCallback<HistoryResponse>() {
    override fun areItemsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean =
        oldItem == newItem
}
