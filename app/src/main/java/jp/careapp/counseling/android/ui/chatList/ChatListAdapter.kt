package jp.careapp.counseling.android.ui.chatList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.data.model.history_chat.HistoryChatResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerRankingHandler
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.ItemHistoryChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter constructor(
    private val context: Context,
    val onClickListener: (HistoryChatResponse) -> Unit
) : BaseAdapterLoadMore<HistoryChatResponse>(ChatListDiffUtil() as DiffUtil.ItemCallback<HistoryChatResponse>) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ChatListHolder(
            ItemHistoryChatBinding.inflate(layoutInflater, parent, false),
            onClickListener
        )
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatListHolder) {
            holder.bindData(context = context, getItem(position))
        }
    }

    inner class ChatListHolder(
        val binding: ItemHistoryChatBinding,
        private val onClickListener: (HistoryChatResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindData(
            context: Context,
            historyChatResponse: HistoryChatResponse?
        ) {
            historyChatResponse?.let { data ->
                binding.apply {
                    data.performer.let { consultant ->
                        if (!data.fromOwnerMail && consultant != null) {
                            binding.ivPerson.loadImage(consultant.imageUrl, R.drawable.ic_avatar_default, false)
                            val bustSize = context.getBustSize(consultant.bust)
                            if (bustSize == "") {
                                binding.tvSize.visibility = GONE
                            } else {
                                binding.tvSize.visibility = VISIBLE
                                binding.tvSize.text = bustSize
                            }
                            binding.ivStateBeginner.visibility = if(consultant.isRookie == 1) View.VISIBLE else View.GONE
                            binding.tvName.text = consultant.name
                            binding.tvAge.text = String.format(context.getString(R.string.age_pattern),consultant.age)
                            binding.ivRanking.setImageResource(PerformerRankingHandler.getImageViewForRank(consultant.ranking,consultant.recommendRanking))

                            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

                            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

                            val statusBg = PerformerStatusHandler.getStatusBg(status)

                            binding.tvStatus.text = statusText

                            binding.tvStatus.setBackgroundResource(statusBg)

                        } else {
                            Glide.with(binding.ivPerson).load(
                                context.resources.getIdentifier(
                                    "thumb",
                                    "drawable", context.packageName
                                )
                            )
                                .into(binding.ivPerson)
                        }
                    }
                }
                if (historyChatResponse.unreadCount > 0) {
                    binding.ivNews.visibility = VISIBLE
                } else {
                    binding.ivNews.visibility = GONE
                }

                binding.tvTime.text = context.formatDateSendMessage(data.sendDate)

                binding.tvBody.text = data.body

                binding.root.setOnClickListener {
                    notifyItemChanged(adapterPosition)
                    onClickListener(data)
                }
            }
        }
    }
}

fun isYesterday(dateString: String): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val date = sdf.parse(dateString)
    val calDate = Calendar.getInstance()
    calDate.time = date
    val yesterday = Calendar.getInstance()
    yesterday.add(Calendar.DATE, -1)
    return calDate.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
            calDate.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
}

fun Context.formatDateSendMessage(dateSendInput: String): String {
    val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val date = sdf1.parse(dateSendInput)
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dateSend = sdf2.format(date)
    val currentDate = sdf2.format(Date())
    if (currentDate == dateSend) {
        val hhmmFormat = SimpleDateFormat("HH:mm", Locale.US)
        return hhmmFormat.format(date)

    } else if (isYesterday(dateSendInput)) {
        return this.resources.getString(R.string.yesterday_time_send)
    } else {
        val mmyyFormat = SimpleDateFormat("MM/yy", Locale.US)
        return mmyyFormat.format(date)

    }
}

class ChatListDiffUtil : DiffUtil.ItemCallback<HistoryChatResponse?>() {

    override fun areItemsTheSame(
        oldItem: HistoryChatResponse,
        newItem: HistoryChatResponse
    ): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(
        oldItem: HistoryChatResponse,
        newItem: HistoryChatResponse
    ): Boolean {
        return oldItem == newItem
    }
}
