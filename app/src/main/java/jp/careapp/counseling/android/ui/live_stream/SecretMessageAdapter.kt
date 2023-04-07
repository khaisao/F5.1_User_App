package jp.careapp.counseling.android.ui.live_stream

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.network.LiveStreamChatResponse
import jp.careapp.counseling.android.ui.message.BaseMessageAdapterLoadMore
import jp.careapp.counseling.android.ui.message.MessageDiffUtil
import jp.careapp.counseling.databinding.ItemMessageOwnerBinding
import jp.careapp.counseling.databinding.ItemSecretMessagePerformerBinding


class SecretMessageAdapter constructor(
    private val context: Context,
) : BaseMessageAdapterLoadMore<LiveStreamChatResponse>(MessageDiffUtil() as DiffUtil.ItemCallback<LiveStreamChatResponse>) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageOwnerViewHolder -> {
                holder.bindData(getItem(position))
                holder.binding.contentMessageTv.requestLayout()
            }
            is MessagePerformerViewHolder -> {
                holder.bindData(getItem(position))
                holder.binding.contentMessageTv.requestLayout()
            }
        }
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_OWNER -> {
                MessageOwnerViewHolder(
                    ItemMessageOwnerBinding.inflate(layoutInflater, parent, false)
                )
            }

            else -> {
                MessagePerformerViewHolder(
                    ItemSecretMessagePerformerBinding.inflate(layoutInflater, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is LiveStreamChatResponse) {
            if (!item.isPerformer) {
                MESSAGE_OWNER
            } else {
                MESSAGE_PERFORMER
            }
        } else {
            MESSAGE_OWNER
        }
    }

    inner class MessageOwnerViewHolder(
        val binding: ItemMessageOwnerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: LiveStreamChatResponse?
        ) {
            binding.apply {
                messageResponse?.let { data ->
                    contentMessageTv.text = data.message
                }
            }
        }
    }

    inner class MessagePerformerViewHolder(
        val binding: ItemSecretMessagePerformerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: LiveStreamChatResponse?
        ) {
            binding.apply {
                messageResponse?.let { data ->
                    contentMessageTv.text = data.message
                }
            }
        }
    }

    companion object {
        const val MESSAGE_OWNER = 1
        const val MESSAGE_PERFORMER = 2
    }
}

class MessageDiffUtil : DiffUtil.ItemCallback<LiveStreamChatResponse?>() {

    override fun areItemsTheSame(
        oldItem: LiveStreamChatResponse,
        newItem: LiveStreamChatResponse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LiveStreamChatResponse,
        newItem: LiveStreamChatResponse
    ): Boolean {
            return oldItem == newItem
    }
}
