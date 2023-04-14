package jp.careapp.counseling.android.ui.live_stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.databinding.ItemLiveStreamMessageBinding
import jp.careapp.counseling.databinding.ItemLiveStreamWhisperBinding

class LiveStreamAdapter :
    ListAdapter<LiveStreamChatResponse, RecyclerView.ViewHolder>(LiveStreamDiffUtil()) {

    class MessageViewHolder(private val binding: ItemLiveStreamMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_dadada
            )
        }
    }

    class PerformerMessageViewHolder(private val binding: ItemLiveStreamMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_ff8de8
            )
        }
    }

    class WhisperViewHolder(private val binding: ItemLiveStreamWhisperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_dadada
            )
        }
    }

    class PerformerWhisperViewHolder(private val binding: ItemLiveStreamWhisperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_ff8de8
            )
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position).isPerformer) {
        false -> if (getItem(position).isWhisper) LiveStreamCommentType.WHISPER else LiveStreamCommentType.NORMAL
        else -> if (getItem(position).isWhisper) LiveStreamCommentType.PERFORMER_WHISPER else LiveStreamCommentType.PERFORMER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LiveStreamCommentType.NORMAL -> {
                MessageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_message,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.PERFORMER -> {
                PerformerMessageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_message,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.WHISPER -> {
                WhisperViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_whisper,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.PERFORMER_WHISPER -> {
                PerformerWhisperViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_whisper,
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.isPerformer) {
            false -> {
                if (!item.isWhisper) {
                    val myViewHolder = holder as MessageViewHolder
                    myViewHolder.bind(item)
                } else {
                    val myViewHolder = holder as WhisperViewHolder
                    myViewHolder.bind(item)
                }
            }
            else -> {
                if (!item.isWhisper) {
                    val myViewHolder = holder as PerformerMessageViewHolder
                    myViewHolder.bind(item)
                } else {
                    val myViewHolder = holder as PerformerWhisperViewHolder
                    myViewHolder.bind(item)
                }
            }
        }
    }

    override fun submitList(list: MutableList<LiveStreamChatResponse>?) {
        val result = arrayListOf<LiveStreamChatResponse>()
        list?.forEach {
            result.add(it)
        }
        super.submitList(result)
    }
}

class LiveStreamDiffUtil : DiffUtil.ItemCallback<LiveStreamChatResponse>() {
    override fun areItemsTheSame(
        oldItem: LiveStreamChatResponse,
        newItem: LiveStreamChatResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LiveStreamChatResponse,
        newItem: LiveStreamChatResponse
    ): Boolean {
        return oldItem == newItem
    }
}