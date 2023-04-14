package jp.careapp.counseling.android.ui.review_mode.live_stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.android.ui.live_stream.LiveStreamCommentType
import jp.careapp.counseling.android.ui.live_stream.setLiveStreamCommentText
import jp.careapp.counseling.databinding.ItemLiveStreamMessageBinding
import jp.careapp.counseling.databinding.ItemLiveStreamWhisperBinding

class RMLiveStreamAdapter :
    ListAdapter<LiveStreamChatResponse, RecyclerView.ViewHolder>(LiveStreamDiffUtil()) {
    class NormalMessageViewHolder(private val binding: ItemLiveStreamMessageBinding) :
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
                R.color.color_89e9ff
            )
        }
    }

    class WhisperMessageViewHolder(private val binding: ItemLiveStreamWhisperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_dadada
            )
        }
    }

    class PerformerWhisperMessageViewHolder(private val binding: ItemLiveStreamWhisperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                message.name!!,
                message.message!!,
                R.color.color_89e9ff
            )
        }
    }

    override fun getItemViewType(position: Int) = when {
        !getItem(position).isPerformer && !getItem(position).isWhisper -> LiveStreamCommentType.NORMAL
        getItem(position).isPerformer && !getItem(position).isWhisper -> LiveStreamCommentType.PERFORMER
        !getItem(position).isPerformer && getItem(position).isWhisper -> LiveStreamCommentType.WHISPER
        getItem(position).isPerformer && getItem(position).isWhisper -> LiveStreamCommentType.PERFORMER_WHISPER
        else -> throw Throwable("")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LiveStreamCommentType.NORMAL -> {
                NormalMessageViewHolder(
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
                WhisperMessageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_whisper,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.PERFORMER_WHISPER -> {
                WhisperMessageViewHolder(
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
        when (getItemViewType(position)) {
            LiveStreamCommentType.NORMAL -> {
                val myViewHolder = holder as NormalMessageViewHolder
                myViewHolder.bind(item)
            }
            LiveStreamCommentType.PERFORMER -> {
                val myViewHolder = holder as PerformerMessageViewHolder
                myViewHolder.bind(item)
            }
            LiveStreamCommentType.WHISPER -> {
                val myViewHolder = holder as WhisperMessageViewHolder
                myViewHolder.bind(item)
            }
            LiveStreamCommentType.PERFORMER_WHISPER -> {
                val myViewHolder = holder as PerformerWhisperMessageViewHolder
                myViewHolder.bind(item)
            }
            else -> {}
        }
    }

    override fun submitList(list: MutableList<LiveStreamChatResponse>?) {
        val result = arrayListOf<LiveStreamChatResponse>()
        list?.forEach {
            result.add(it.copy())
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