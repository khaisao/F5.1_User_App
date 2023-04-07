package jp.careapp.counseling.android.ui.review_mode.live_stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.LiveStreamCommentResponse
import jp.careapp.counseling.android.ui.live_stream.LiveStreamCommentType
import jp.careapp.counseling.android.ui.live_stream.setLiveStreamCommentText
import jp.careapp.counseling.databinding.ItemLiveStreamNormalCommentBinding
import jp.careapp.counseling.databinding.ItemLiveStreamPerformerCommentBinding
import jp.careapp.counseling.databinding.ItemLiveStreamWhisperCommentBinding

class RMLiveStreamAdapter :
    ListAdapter<LiveStreamCommentResponse, RecyclerView.ViewHolder>(LiveStreamDiffUtil()) {
    class NormalCommentViewHolder(private val binding: ItemLiveStreamNormalCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamCommentResponse) {
            binding.tvComment.setLiveStreamCommentText(
                "視聴者名 ${comment.comment}",
                R.color.color_dadada
            )
        }
    }

    class PerformerCommentViewHolder(private val binding: ItemLiveStreamPerformerCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamCommentResponse) {
            binding.tvComment.setLiveStreamCommentText(
                "配信者名 ${comment.comment}",
                R.color.color_ff8de8
            )
        }
    }

    class WhisperCommentViewHolder(private val binding: ItemLiveStreamWhisperCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: LiveStreamCommentResponse) {
            binding.tvComment.setLiveStreamCommentText(
                "視聴者名 ${comment.comment}",
                R.color.color_dadada
            )
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position).type) {
        LiveStreamCommentType.NORMAL -> LiveStreamCommentType.NORMAL
        LiveStreamCommentType.PERFORMER -> LiveStreamCommentType.PERFORMER
        LiveStreamCommentType.WHISPER -> LiveStreamCommentType.WHISPER
        else -> throw Throwable("")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LiveStreamCommentType.NORMAL -> {
                NormalCommentViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_normal_comment,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.PERFORMER -> {
                PerformerCommentViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_live_stream_performer_comment,
                        parent,
                        false
                    )
                )
            }
            LiveStreamCommentType.WHISPER -> {
                WhisperCommentViewHolder(
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
        when (item.type) {
            LiveStreamCommentType.NORMAL -> {
                val myViewHolder = holder as NormalCommentViewHolder
                myViewHolder.bind(item)
            }
            LiveStreamCommentType.PERFORMER -> {
                val myViewHolder = holder as PerformerCommentViewHolder
                myViewHolder.bind(item)
            }
            LiveStreamCommentType.WHISPER -> {
                val myViewHolder = holder as WhisperCommentViewHolder
                myViewHolder.bind(item)
            }
            else -> {}
        }
    }

    override fun submitList(list: MutableList<LiveStreamCommentResponse>?) {
        val result = arrayListOf<LiveStreamCommentResponse>()
        list?.forEach {
            when (it.type) {
                LiveStreamCommentType.NORMAL -> result.add(it.copy())
                LiveStreamCommentType.PERFORMER -> result.add(it.copy())
                LiveStreamCommentType.WHISPER -> result.add(it.copy())
            }
        }
        super.submitList(result)
    }
}

class LiveStreamDiffUtil : DiffUtil.ItemCallback<LiveStreamCommentResponse>() {
    override fun areItemsTheSame(
        oldItem: LiveStreamCommentResponse,
        newItem: LiveStreamCommentResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LiveStreamCommentResponse,
        newItem: LiveStreamCommentResponse
    ): Boolean {
        return oldItem == newItem
    }
}