package jp.slapp.android.android.ui.review_mode.user_detail_message

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_1
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_5
import jp.careapp.core.utils.DateUtil.Companion.convertStringToDateString
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.data.model.message.BaseMessageResponse
import jp.slapp.android.android.data.model.message.RMMessageResponse
import jp.slapp.android.android.data.model.message.TimeMessageResponse
import jp.slapp.android.databinding.ItemMessageOwnerRmBinding
import jp.slapp.android.databinding.ItemMessagePerformerRmBinding
import jp.slapp.android.databinding.ItemTimeMessageBinding

class RMUserDetailMsgAdapter constructor(
    private val context: Context
) : UserDetailMsgAdapterLoadMore<BaseMessageResponse>(MessageDiffUtil() as DiffUtil.ItemCallback<BaseMessageResponse>) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is RMMessageResponse -> {
                if (item.sendMail == true) {
                    MESSAGE_OWNER
                } else {
                    MESSAGE_PERFORMER
                }
            }
            else -> MESSAGE_TIME
        }
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageOwnerViewHolder -> {
                holder.bindData(getItem(position) as RMMessageResponse?)
                holder.binding.contentMessageTv.requestLayout()
            }
            is MessagePerformerViewHolder -> {
                holder.bindData(getItem(position) as RMMessageResponse?)
                holder.binding.contentMessageTv.requestLayout()
            }
            is TimeViewHolder -> {
                holder.bindData(getItem(position) as TimeMessageResponse?)
            }
        }
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_OWNER ->
                MessageOwnerViewHolder(
                    ItemMessageOwnerRmBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            MESSAGE_PERFORMER ->
                MessagePerformerViewHolder(
                    ItemMessagePerformerRmBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            else ->
                TimeViewHolder(ItemTimeMessageBinding.inflate(layoutInflater, parent, false))
        }
    }

    inner class MessageOwnerViewHolder(
        val binding: ItemMessageOwnerRmBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(messageResponse: RMMessageResponse?) {
            binding.apply {
                messageResponse?.let { data ->
                    timeSendTv.text = convertStringToDateString(
                        data.sendDate,
                        DATE_FORMAT_1,
                        DATE_FORMAT_5
                    )
                    val messageBody = data.body
                    contentMessageTv.text = messageBody
                    statusMessageTv.isVisible = data.open ?: false
                }
            }
        }
    }

    inner class MessagePerformerViewHolder(
        val binding: ItemMessagePerformerRmBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(messageResponse: RMMessageResponse?) {
            binding.apply {
                messageResponse?.let { data ->
                    timeSendTv.text = convertStringToDateString(
                        data.sendDate,
                        DATE_FORMAT_1,
                        DATE_FORMAT_5
                    )
                    contentMessageTv.text = data.body
                }
            }
        }
    }

    inner class TimeViewHolder(
        val binding: ItemTimeMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(messageResponse: TimeMessageResponse?) {
            messageResponse?.let { data ->
                binding.apply {
                    timeTv.text = DateUtil.getTimeMessageChat(data.time)
                }
            }
        }
    }

    companion object {
        const val MESSAGE_OWNER = 1
        const val MESSAGE_PERFORMER = 2
        const val MESSAGE_TIME = 3
    }
}

class MessageDiffUtil : DiffUtil.ItemCallback<BaseMessageResponse?>() {

    override fun areItemsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        return if (oldItem is RMMessageResponse && newItem is RMMessageResponse) {
            oldItem.code == newItem.code
        } else {
            false
        }
    }

    override fun areContentsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        if (oldItem is RMMessageResponse && newItem is RMMessageResponse) {
            return oldItem == newItem
        }
        return false
    }
}
