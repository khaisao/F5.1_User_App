package jp.careapp.counseling.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.GenResItem
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.databinding.ItemConsultantBinding

class ConsultantAdapter(
    val context: Context,
    val listGenRes: ArrayList<GenResItem>,
    private val listener: (Int, List<ConsultantResponse>) -> Unit
) :
    ListAdapter<ConsultantResponse, ConsultantAdapter.ConsultantHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<ConsultantResponse>() {
        override fun areItemsTheSame(
            oldItem: ConsultantResponse,
            newItem: ConsultantResponse
        ): Boolean {
            return oldItem.name == newItem.name && oldItem.code == newItem.code
        }

        override fun areContentsTheSame(
            oldItem: ConsultantResponse,
            newItem: ConsultantResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ConsultantHolder(
        val binding: ItemConsultantBinding,
        private val listener: (Int, List<ConsultantResponse>) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(consultant: ConsultantResponse) {
            binding.tvName.text = consultant.name
            if (consultant.ranking != null && consultant.ranking.ranking > 0 && consultant.ranking.ranking < 31) {
                binding.ivRanking.visibility = View.VISIBLE
                when (consultant.ranking.interval) {
                    0 -> {
                        when (consultant.ranking.ranking) {
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
                        when (consultant.ranking.ranking) {
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
                        when (consultant.ranking.ranking) {
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
                binding.ivRanking.visibility = View.INVISIBLE
            }

            Glide.with(context).load(consultant.imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.default_avt_performer)
                )
                .into(binding.ivAvatar)

            binding.rlConsultant.setOnClickListener {
                listener.invoke(adapterPosition, currentList)
            }

            binding.tvAge.text = String.format(context.getString(R.string.age_pattern),consultant.age)

            binding.apply {
                tvTweet.visibility =
                    if (consultant.messageOfTheDay.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                tvTweet.text = consultant.messageOfTheDay?.replace("\n", "")
            }
            if (ConsultantResponse.isWaiting(consultant.callStatus, consultant.chatStatus)) {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_waiting)
                binding.tvStatus.text =
                    context.resources.getString(R.string.presence_status_waiting)
            } else if (ConsultantResponse.isLiveStream(
                    consultant.callStatus,
                    consultant.chatStatus
                )
            ) {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
                binding.tvStatus.text =
                    context.resources.getString(R.string.presence_status_live_streaming)
            } else if (ConsultantResponse.isPrivateLiveStream(
                    consultant.callStatus,
                    consultant.chatStatus
                )
            ) {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
                binding.tvStatus.text =
                    context.resources.getString(R.string.presence_status_private_delivery)
            } else {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
                binding.tvStatus.text =
                    context.resources.getString(R.string.presence_status_offline)
            }
            val bustSize = context.getBustSize(consultant.bust)
            if (bustSize == "") {
                binding.tvSize.visibility = View.GONE
            } else {
                binding.tvSize.visibility = View.VISIBLE
                binding.tvSize.text = bustSize
            }
        }
    }

    override fun submitList(list: List<ConsultantResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemConsultantBinding.inflate(inflater, parent, false)
        return ConsultantHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ConsultantHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
