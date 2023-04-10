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
import jp.careapp.counseling.databinding.ItemLiveStreamNormalCommentBinding
import jp.careapp.counseling.databinding.ItemLiveStreamPerformerCommentBinding
import jp.careapp.counseling.databinding.ItemLiveStreamWhisperCommentBinding

class RMLiveStreamAdapter :
    ListAdapter<LiveStreamChatResponse, RecyclerView.ViewHolder>(LiveStreamDiffUtil()) {
    class NormalMessageViewHolder(private val binding: ItemLiveStreamNormalCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamChatResponse) {
            binding.tvComment.setLiveStreamCommentText(
                "視聴者名 ${comment.message}",
                R.color.color_dadada
            )
        }
    }

    class PerformerMessageViewHolder(private val binding: ItemLiveStreamPerformerCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                "配信者名 ${comment.message}",
                R.color.color_ff8de8
            )
        }
    }

    class WhisperMessageViewHolder(private val binding: ItemLiveStreamWhisperCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                "視聴者名 ${comment.message}",
                R.color.color_dadada
            )
        }
    }

    class PerformerWhisperMessageViewHolder(private val binding: ItemLiveStreamWhisperCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: LiveStreamChatResponse) {
            binding.tvMessage.setLiveStreamCommentText(
                "配信者名 ${message.message}",
                R.color.color_dadada
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
                        R.layout.item_live_stream_normal_comment,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.PERFORMER -> {
                PerformerMessageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_performer_comment,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.WHISPER -> {
                WhisperMessageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_whisper_comment,
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