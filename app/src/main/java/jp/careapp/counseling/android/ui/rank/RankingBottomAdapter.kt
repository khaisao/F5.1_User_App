package jp.careapp.counseling.android.ui.rank

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
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.TypeRankingResponse
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.databinding.ItemRankingBinding

class RankingBottomAdapter(
    val context: Context,
    private val onClickListener: (Int) -> Unit,
    private val typeRankingLayout: Int,
    private val spanCount: Int
) : ListAdapter<TypeRankingResponse, RankingBottomAdapter.RankingHolder>((RankingDiffUtil())) {

    class RankingDiffUtil : DiffUtil.ItemCallback<TypeRankingResponse>() {
        override fun areItemsTheSame(
            oldItem: TypeRankingResponse,
            newItem: TypeRankingResponse
        ): Boolean {
            return oldItem.ranking == newItem.ranking
                    && oldItem.point == newItem.point
                    && oldItem.performerResponse == newItem.performerResponse
        }

        override fun areContentsTheSame(
            oldItem: TypeRankingResponse,
            newItem: TypeRankingResponse
        ): Boolean {
            return oldItem.ranking == newItem.ranking
                    && oldItem.point == newItem.point
                    && oldItem.performerResponse == newItem.performerResponse
        }
    }

    inner class RankingHolder(
        val binding: ItemRankingBinding,
        private val onClickListener: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(typeRanking: TypeRankingResponse) {

            typeRanking.let {
                typeRanking.performerResponse?.let { performer ->
                    Glide.with(context).load(performer.imageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.ic_avatar_default))
                        .into(binding.ivAvatar)

                    val (statusResId, statusText) = when {
                        ConsultantResponse.isWaiting(performer.callStatus, performer.chatStatus) -> {
                            R.drawable.bg_performer_status_waiting to R.string.presence_status_waiting
                        }
                        ConsultantResponse.isLiveStream(performer.callStatus, performer.chatStatus) -> {
                            R.drawable.bg_performer_status_live_streaming to R.string.presence_status_live_streaming
                        }
                        ConsultantResponse.isPrivateLiveStream(performer.callStatus, performer.chatStatus) -> {
                            R.drawable.bg_performer_status_private_delivery to R.string.presence_status_private_delivery
                        }
                        else -> {
                            R.drawable.bg_performer_status_offline to R.string.presence_status_offline
                        }
                    }
                    binding.tvPresenceStatus.setBackgroundResource(statusResId)
                    binding.tvPresenceStatus.text = binding.root.context.resources.getString(statusText)
                    val bustSize = context.getBustSize(performer.bust)
                    if (bustSize == "") {
                        binding.tvSize.visibility = View.GONE
                    } else {
                        binding.tvSize.visibility = View.VISIBLE
                        binding.tvSize.text = bustSize
                    }
                    binding.tvAge.text = performer.age.toString() + context.resources.getString(R.string.age_raw)
                    binding.tvName.text = performer.name
                }
            }
            if(spanCount == 2){
                binding.tvName.setTextAppearance(R.style.text_bold_12)
                binding.tvSize.setTextAppearance(R.style.text_bold_10)
                binding.tvAge.setTextAppearance(R.style.text_bold_10)

            } else {
                binding.tvName.setTextAppearance(R.style.text_bold_9)
                binding.tvSize.setTextAppearance(R.style.text_bold_7)
                binding.tvAge.setTextAppearance(R.style.text_bold_7)
            }

            if (typeRankingLayout == BUNDLE_KEY.TYPE_DAILY) {
                binding.ivRankFrame.setImageResource(R.drawable.bg_rank_daily)
                binding.clRanking.setBackgroundResource(R.drawable.bg_rank_daily_behind)
                when (typeRanking.ranking) {
                    4 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_4)
                    5 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_5)
                    6 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_6)
                    7 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_7)
                    8 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_8)
                    9 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_9)
                    10 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_10)
                    11 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_11)
                    12 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_12)
                    13 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_13)
                    14 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_14)
                    15 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_15)
                    16 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_16)
                    17 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_17)
                    18 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_18)
                    19 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_19)
                    20 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_20)
                    21 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_21)
                    22 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_22)
                    23 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_23)
                    24 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_24)
                    25 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_25)
                    26 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_26)
                    27 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_27)
                    28 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_28)
                    29 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_29)
                    30 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_daily_30)
                }
            } else if (typeRankingLayout == BUNDLE_KEY.TYPE_WEEKLY) {
                binding.ivRankFrame.setImageResource(R.drawable.bg_rank_weekly)
                binding.clRanking.setBackgroundResource(R.drawable.bg_rank_weekly_behind)
                when (typeRanking.ranking) {
                    4 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_4)
                    5 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_5)
                    6 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_6)
                    7 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_7)
                    8 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_8)
                    9 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_9)
                    10 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_10)
                    11 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_11)
                    12 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_12)
                    13 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_13)
                    14 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_14)
                    15 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_15)
                    16 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_16)
                    17 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_17)
                    18 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_18)
                    19 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_19)
                    20 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_20)
                    21 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_21)
                    22 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_22)
                    23 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_23)
                    24 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_24)
                    25 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_25)
                    26 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_26)
                    27 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_27)
                    28 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_28)
                    29 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_29)
                    30 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_weekly_30)
                }
            } else if (typeRankingLayout == BUNDLE_KEY.TYPE_MONTHLY) {
                binding.clRanking.setBackgroundResource(R.drawable.bg_rank_monthly_behind)
                binding.ivRankFrame.setImageResource(R.drawable.bg_rank_monthly)
                when (typeRanking.ranking) {
                    4 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_4)
                    5 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_5)
                    6 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_6)
                    7 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_7)
                    8 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_8)
                    9 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_9)
                    10 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_10)
                    11 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_11)
                    12 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_12)
                    13 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_13)
                    14 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_14)
                    15 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_15)
                    16 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_16)
                    17 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_17)
                    18 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_18)
                    19 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_19)
                    20 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_20)
                    21 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_21)
                    22 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_22)
                    23 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_23)
                    24 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_24)
                    25 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_25)
                    26 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_26)
                    27 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_27)
                    28 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_28)
                    29 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_29)
                    30 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_monthly_30)
                }
            } else {
                binding.ivRankFrame.setImageResource(R.drawable.bg_rank_best)
                binding.clRanking.setBackgroundResource(R.drawable.bg_rank_best_behind)
                when (typeRanking.recommendRanking) {
                    4 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_4)
                    5 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_5)
                    6 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_6)
                    7 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_7)
                    8 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_8)
                    9 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_9)
                    10 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_10)
                    11 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_11)
                    12 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_12)
                    13 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_13)
                    14 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_14)
                    15 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_15)
                    16 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_16)
                    17 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_17)
                    18 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_18)
                    19 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_19)
                    20 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_20)
                    21 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_21)
                    22 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_22)
                    23 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_23)
                    24 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_24)
                    25 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_25)
                    26 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_26)
                    27 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_27)
                    28 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_28)
                    29 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_29)
                    30 -> binding.ivRankNumber.loadImage(R.drawable.ic_ranking_best_30)
                }
            }

            binding.clRanking.setOnClickListener {
                typeRanking.let {
                    onClickListener.invoke(absoluteAdapterPosition)
                }
            }
        }
    }

    override fun submitList(list: List<TypeRankingResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRankingBinding.inflate(inflater, parent, false)
        return RankingHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: RankingHolder, position: Int) {
        holder.bind(getItem(position))
    }

}