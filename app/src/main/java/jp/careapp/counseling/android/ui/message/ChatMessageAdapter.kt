package jp.careapp.counseling.android.ui.message

import android.content.Context
import android.graphics.BlurMaskFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.MaskFilterSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_1
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_5
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.message.BaseMessageResponse
import jp.careapp.counseling.android.data.model.message.MessageResponse
import jp.careapp.counseling.android.data.model.message.TimeMessageResponse
import jp.careapp.counseling.android.utils.extensions.toPayLength
import jp.careapp.counseling.android.utils.extensions.toPayPoint
import jp.careapp.counseling.databinding.ItemMessageCallBinding
import jp.careapp.counseling.databinding.ItemMessageOwnerBinding
import jp.careapp.counseling.databinding.ItemMessagePerformerBinding
import jp.careapp.counseling.databinding.ItemTimeMessageBinding


class ChatMessageAdapter constructor(
    private val context: Context,
    val onClickListener: (MessageResponse, Int) -> Unit
) : BaseMessageAdapterLoadMore<BaseMessageResponse>(MessageDiffUtil() as DiffUtil.ItemCallback<BaseMessageResponse>) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private var pointPerChar: Int = 0

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
            is TimeViewHolder -> {
                holder.bindData(getItem(position) as TimeMessageResponse?)
            }
            is MessageCallHolder -> {
                holder.bindData(getItem(position) as MessageResponse?)
            }
        }
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            MESSAGE_OWNER -> {
                return MessageOwnerViewHolder(
                    ItemMessageOwnerBinding.inflate(layoutInflater, parent, false),
                    onClickListener
                )
            }
            MESSAGE_PERFORMER -> {
                return MessagePerformerViewHolder(
                    ItemMessagePerformerBinding.inflate(layoutInflater, parent, false),
                    onClickListener
                )
            }
            MESSAGE_CALL -> {
                return MessageCallHolder(
                    ItemMessageCallBinding.inflate(layoutInflater, parent, false),
                    context
                )
            }
            else -> {
                return TimeViewHolder(
                    ItemTimeMessageBinding.inflate(layoutInflater, parent, false),
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is MessageResponse) {
            if (item.subject == SUBJECT_CALL_HISTORY) {
                MESSAGE_CALL
            } else if (item.sendMail) {
                MESSAGE_OWNER
            } else {
                MESSAGE_PERFORMER
            }
        } else {
            MESSAGE_TIME
        }
    }

    fun setPointPerChar(pointPerChar: Int) {
        this.pointPerChar = pointPerChar
        notifyDataSetChanged()
    }

    inner class MessageOwnerViewHolder(
        val binding: ItemMessageOwnerBinding,
        private val onClickListener: (MessageResponse, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: MessageResponse?
        ) {
            binding.apply {
                messageResponse?.let { data ->
                    timeSendTv.setText(
                        DateUtil.convertStringToDateString(
                            data.sendDate,
                            DATE_FORMAT_1,
                            DATE_FORMAT_5
                        )
                    )
                    statusMessageTv.visibility = if (data.open) View.VISIBLE else View.GONE
                    contentMessageTv.setText(data.body)
                }
                containerItemOwnerCl.setOnClickListener {
                    messageResponse?.let { it1 -> onClickListener.invoke(it1, CLICK_CONTAIN_VIEW) }
                }
            }
        }
    }

    inner class MessagePerformerViewHolder(
        val binding: ItemMessagePerformerBinding,
        private val onClickListener: (MessageResponse, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: MessageResponse?,
            context: Context
        ) {
            binding.apply {
                messageResponse?.let { data ->
                    timeSendTv.text = DateUtil.convertStringToDateString(
                        data.sendDate,
                        DATE_FORMAT_1,
                        DATE_FORMAT_5
                    )
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
                        llUnlockFeeMessage.visibility = View.VISIBLE
                        tvUnlockMessage.text = context.getString(
                            R.string.unlock_message,
                            data.body.toPayLength(),
                            data.body.toPayPoint(pointPerChar)
                        )
                        llUnlockFeeMessage.setOnClickListener {
                            messageResponse?.let { it ->
                                onClickListener.invoke(
                                    it,
                                    CLICK_PAY_MESSAGE
                                )
                            }
                        }
                    } else {
                        contentMessageTv.text = data.body
                        contentMessageTv.setTextIsSelectable(true)
                        llUnlockFeeMessage.visibility = View.GONE
                    }
                    if (data.performer != null)
                        avatarIv.loadImage(
                            data.performer.imageUrl,
                            R.drawable.ic_avatar_default,
                            true
                        )
                    else
                        Glide.with(avatarIv).load(
                            context.resources.getIdentifier(
                                "thumb",
                                "drawable", context.packageName
                            )
                        )
                            .placeholder(R.drawable.ic_avatar_default)
                            .circleCrop()
                            .into(avatarIv)
                }
                avatarIv.setOnClickListener {
                    messageResponse?.let { it1 -> onClickListener.invoke(it1, CLICK_AVATAR) }
                }
                contentMessageTv.setOnClickListener {
                    messageResponse?.let { it1 -> onClickListener.invoke(it1, CLICK_MESSAGE) }
                }
                containerItemPerformerCl.setOnClickListener {
                    messageResponse?.let { it1 -> onClickListener.invoke(it1, CLICK_CONTAIN_VIEW) }
                }
            }
        }
    }

    inner class TimeViewHolder(
        val binding: ItemTimeMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            messageResponse: TimeMessageResponse?
        ) {
            messageResponse?.let { data ->
                binding.apply {
                    timeTv.setText(DateUtil.getTimeMessageChat(data.time))
                }
            }
        }
    }


    inner class MessageCallHolder(
        val binding: ItemMessageCallBinding,
        val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: MessageResponse?) {
            data?.apply {
                binding.apply {
                    tvTimeSend.text =
                        DateUtil.convertStringToDateString(sendDate, DATE_FORMAT_1, DATE_FORMAT_5)
                    body.split("\n").let { body ->
                        if (body.isNotEmpty()) {
                            if (body.size >= CALL_MESSAGE_BODY_SIZE) {
                                val isErrorCall = body[1] == ERROR || body[1] == NOT_ENOUGH_POINTS
                                tvTitle.text = body[0]
                                tvReason.apply {
                                    text = body[1]
                                    setTextColor(
                                        if (isErrorCall) context.getColor(R.color.color_FFD600)
                                        else context.getColor(R.color.white)
                                    )
                                }
                                tvDuration.text = body[2]
                                showDescription(true, isErrorCall)
                            } else {
                                tvTitle.text = body[0]
                                showDescription(false)
                            }
                        }
                    }
                }
            }
        }

        private fun showDescription(isShow: Boolean, isErrorCall: Boolean = false) {
            binding.apply {
                tvReason.isVisible = isShow
                tvDuration.isVisible = isShow
                ivCaution.isVisible = isShow && isErrorCall
            }
        }
    }

    companion object {
        const val MESSAGE_OWNER = 1
        const val MESSAGE_PERFORMER = 2
        const val MESSAGE_TIME = 3
        const val MESSAGE_CALL = 4
        const val CLICK_AVATAR = 1
        const val CLICK_MESSAGE = 2
        const val CLICK_CONTAIN_VIEW = 3
        const val CLICK_PAY_MESSAGE = 4

        const val CALL_MESSAGE_BODY_SIZE = 3
        const val SUBJECT_CALL_HISTORY = "SYSTEM_通話履歴"
        const val NOT_ENOUGH_POINTS = "ポイント不足終了"
        const val ERROR = "エラー終了"
    }
}

class MessageDiffUtil : DiffUtil.ItemCallback<BaseMessageResponse?>() {

    override fun areItemsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        if (oldItem is MessageResponse && newItem is MessageResponse) {
            return oldItem.code.equals(newItem.code)
        } else {
            return false
        }
    }

    override fun areContentsTheSame(
        oldItem: BaseMessageResponse,
        newItem: BaseMessageResponse
    ): Boolean {
        if (oldItem is MessageResponse && newItem is MessageResponse) {
            return oldItem.equals(newItem)
        }
        return false
    }
}
