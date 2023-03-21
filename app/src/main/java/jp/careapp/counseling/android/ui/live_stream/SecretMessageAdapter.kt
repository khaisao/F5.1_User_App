package jp.careapp.counseling.android.ui.live_stream

import android.content.Context
import android.graphics.BlurMaskFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.MaskFilterSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.message.BaseMessageResponse
import jp.careapp.counseling.android.data.model.message.MessageResponse
import jp.careapp.counseling.android.ui.message.BaseMessageAdapterLoadMore
import jp.careapp.counseling.android.ui.message.MessageDiffUtil
import jp.careapp.counseling.databinding.ItemMessageOwnerBinding
import jp.careapp.counseling.databinding.ItemSecretMessagePerformerBinding


class SecretMessageAdapter constructor(
    private val context: Context,
) : BaseMessageAdapterLoadMore<BaseMessageResponse>(MessageDiffUtil() as DiffUtil.ItemCallback<BaseMessageResponse>) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageOwnerViewHolder -> {
                holder.bindData(getItem(position) as MessageResponse?)
                holder.binding.contentMessageTv.requestLayout()
            }
            is MessagePerformerViewHolder -> {
                holder.bindData(getItem(position) as MessageResponse?, context)
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
        return if (item is MessageResponse) {
            if (item.sendMail) {
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
            messageResponse: MessageResponse?
        ) {
            binding.apply {
                messageResponse?.let { data ->
                    contentMessageTv.text = data.body
                }
            }
        }
    }

    inner class MessagePerformerViewHolder(
        val binding: ItemSecretMessagePerformerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: MessageResponse?,
            context: Context
        ) {
            binding.apply {
                messageResponse?.let { data ->

                    if (data.payFlag && !data.open) {
                        val radius = contentMessageTv.textSize / 1.5f
                        val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                        val blurString = SpannableString(data.body)
                        blurString.setSpan(
                            MaskFilterSpan(filter),
                            data.payPreviewCount,
                            blurString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        contentMessageTv.text = blurString
                        contentMessageTv.setTextIsSelectable(false)

                    } else {
                        contentMessageTv.text = data.body
                        contentMessageTv.setTextIsSelectable(true)
                    }

                }
            }
        }
    }

    companion object {
        const val MESSAGE_OWNER = 1
        const val MESSAGE_PERFORMER = 2
    }
}

class MessageDiffUtil : DiffUtil.ItemCallback<BaseMessageResponse?>() {

    override fun areItemsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        return if (oldItem is MessageResponse && newItem is MessageResponse) {
            oldItem.code == newItem.code
        } else {
            false
        }
    }

    override fun areContentsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        if (oldItem is MessageResponse && newItem is MessageResponse) {
            return oldItem == newItem
        }
        return false
    }
}
