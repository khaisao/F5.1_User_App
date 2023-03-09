package jp.careapp.counseling.android.ui.chatList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.data.model.history_chat.HistoryChatResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
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
                    data.performer.let {
                        if (!data.fromOwnerMail && it != null) {
                            binding.ivPerson.loadImage(it.imageUrl, R.drawable.ic_avatar_default, false)
                            val bustSize = binding.root.context.getBustSize(it.bust)
                            if (bustSize == "") {
                                binding.tvSize.visibility = GONE
                            } else {
                                binding.tvSize.visibility = VISIBLE
                                binding.tvSize.text = bustSize
                            }
                            binding.tvName.text = it.name
                            binding.tvAge.text = String.format(context.getString(R.string.age_pattern),it.age)

                            if (it.ranking != null && it.ranking.ranking > 0 && it.ranking.ranking < 31) {
                                binding.ivRanking.visibility = VISIBLE
                                when (it.ranking.interval) {
                                    0 -> {
                                        when (it.ranking.ranking) {
                                            1 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_1)
                                            2 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_2)
                                            3 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_3)
                                            4 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_4)
                                            5 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_5)
                                            6 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_6)
                                            7 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_7)
                                            8 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_8)
                                            9 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_9)
                                            10 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_10)
                                            11 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_11)
                                            12 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_12)
                                            13 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_13)
                                            14 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_14)
                                            15 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_15)
                                            16 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_16)
                                            17 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_17)
                                            18 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_18)
                                            19 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_19)
                                            20 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_20)
                                            21 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_21)
                                            22 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_22)
                                            23 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_23)
                                            24 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_24)
                                            25 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_25)
                                            26 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_26)
                                            27 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_27)
                                            28 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_28)
                                            29 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_29)
                                            30 -> binding.ivRanking.loadImage(R.drawable.ic_rank_daily_30)
                                        }
                                    }
                                    1 -> {
                                        when (it.ranking.ranking) {
                                            1 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_1)
                                            2 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_2)
                                            3 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_3)
                                            4 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_4)
                                            5 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_5)
                                            6 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_6)
                                            7 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_7)
                                            8 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_8)
                                            9 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_9)
                                            10 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_10)
                                            11 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_11)
                                            12 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_12)
                                            13 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_13)
                                            14 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_14)
                                            15 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_15)
                                            16 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_16)
                                            17 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_17)
                                            18 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_18)
                                            19 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_19)
                                            20 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_20)
                                            21 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_21)
                                            22 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_22)
                                            23 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_23)
                                            24 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_24)
                                            25 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_25)
                                            26 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_26)
                                            27 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_27)
                                            28 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_28)
                                            29 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_29)
                                            30 -> binding.ivRanking.loadImage(R.drawable.ic_rank_weekly_30)
                                        }
                                    }
                                    else -> {
                                        when (it.ranking.ranking) {
                                            1 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_1)
                                            2 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_2)
                                            3 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_3)
                                            4 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_4)
                                            5 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_5)
                                            6 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_6)
                                            7 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_7)
                                            8 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_8)
                                            9 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_9)
                                            10 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_10)
                                            11 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_11)
                                            12 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_12)
                                            13 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_13)
                                            14 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_14)
                                            15 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_15)
                                            16 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_16)
                                            17 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_17)
                                            18 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_18)
                                            19 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_19)
                                            20 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_20)
                                            21 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_21)
                                            22 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_22)
                                            23 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_23)
                                            24 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_24)
                                            25 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_25)
                                            26 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_26)
                                            27 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_27)
                                            28 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_28)
                                            29 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_29)
                                            30 -> binding.ivRanking.loadImage(R.drawable.ic_rank_monthly_30)
                                        }
                                    }
                                }
                            } else {
                                binding.ivRanking.visibility = INVISIBLE
                            }
                            if (ConsultantResponse.isWaiting(it.callStatus,it.chatStatus)) {
                                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_waiting)
                                binding.tvStatus.text =
                                    binding.root.context.resources.getString(R.string.presence_status_waiting)
                            } else if (ConsultantResponse.isWaiting(it.callStatus,it.chatStatus)) {
                                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
                                binding.tvStatus.text =
                                    binding.root.context.resources.getString(R.string.presence_status_live_streaming)
                            } else if (ConsultantResponse.isWaiting(it.callStatus,it.chatStatus)) {
                                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
                                binding.tvStatus.text =
                                    binding.root.context.resources.getString(R.string.presence_status_private_delivery)
                            } else {
                                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
                                binding.tvStatus.text =
                                    binding.root.context.resources.getString(R.string.presence_status_offline)
                            }
                            if (it.stage != 1) {
                                ivState.visibility = GONE
                                ivStateBeginner.visibility = VISIBLE
                            } else {
                                ivState.visibility = VISIBLE
                                ivStateBeginner.visibility = GONE
                            }
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
