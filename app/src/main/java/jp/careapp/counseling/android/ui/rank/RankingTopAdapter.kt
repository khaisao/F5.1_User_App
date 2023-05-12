package jp.careapp.counseling.android.ui.rank

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.TypeRankingResponse
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.TYPE_RECOMMEND
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.*

class RankingTopAdapter(
    val context: Context,
    private val onClickListener: (Int) -> Unit,
    private val typeRankingLayout: Int
) : ListAdapter<TypeRankingResponse, RecyclerView.ViewHolder>((RankingDiffUtil())) {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

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

    inner class RankingTop12Holder(
        val binding: ItemRankingTop12Binding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(typeRanking: TypeRankingResponse) {
            val nullValue = TypeRankingResponse()
            if (typeRanking != nullValue) {
                typeRanking.performerResponse?.let { consultant ->
                    Glide.with(context).load(consultant.imageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.default_avt_performer))
                        .into(binding.ivAvatar)

                    val status = PerformerStatusHandler.getStatus(
                        consultant.callStatus,
                        consultant.chatStatus
                    )

                    val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

                    val statusBg = PerformerStatusHandler.getStatusBg(status)

                    binding.tvStatus.text = statusText

                    binding.tvStatus.setBackgroundResource(statusBg)


                    val bustSize = context.getBustSize(consultant.bust)
                    if (bustSize == "") {
                        binding.tvSize.visibility = View.GONE
                    } else {
                        binding.tvSize.visibility = View.VISIBLE
                        binding.tvSize.text = bustSize
                    }
                    binding.tvAge.text =
                        consultant.age.toString() + context.resources.getString(R.string.age_raw)
                    binding.tvName.text = consultant.name
                }

                val drawableRes =
                    if (typeRanking.ranking == 1 || typeRanking.recommendRanking == 1) {
                        when (typeRankingLayout) {
                            BUNDLE_KEY.TYPE_DAILY -> R.drawable.ic_ranking_daily_1
                            BUNDLE_KEY.TYPE_WEEKLY -> R.drawable.ic_ranking_week_1
                            BUNDLE_KEY.TYPE_MONTHLY -> R.drawable.ic_ranking_monthly_1
                            else -> R.drawable.ic_ranking_best_1
                        }
                    } else {
                        when (typeRankingLayout) {
                            BUNDLE_KEY.TYPE_DAILY -> R.drawable.ic_ranking_daily_2
                            BUNDLE_KEY.TYPE_WEEKLY -> R.drawable.ic_ranking_week_2
                            BUNDLE_KEY.TYPE_MONTHLY -> R.drawable.ic_ranking_monthly_2
                            else -> R.drawable.ic_ranking_best_2
                        }
                    }

                binding.ivBackgroundMain.setImageResource(drawableRes)

                binding.clRanking.setOnClickListener {
                    typeRanking.let {
                        onClickListener.invoke(absoluteAdapterPosition)
                    }
                }
            } else {
                val drawableRes =
                    when (typeRankingLayout) {
                        BUNDLE_KEY.TYPE_DAILY -> R.drawable.bg_rank_daily_behind
                        BUNDLE_KEY.TYPE_WEEKLY -> R.drawable.bg_rank_weekly_behind
                        BUNDLE_KEY.TYPE_MONTHLY -> R.drawable.bg_rank_monthly_behind
                        else -> R.drawable.bg_rank_best_behind
                    }
                binding.ivBackgroundMain.setImageResource(drawableRes)
            }
        }
    }

    inner class RankingTop3Holder(
        val binding: ItemRankingTop3Binding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(typeRanking: TypeRankingResponse) {
            typeRanking.let {
                typeRanking.performerResponse?.let { consultant ->
                    Glide.with(context).load(consultant.imageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.default_avt_performer))
                        .into(binding.ivAvatar)

                    val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

                    val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

                    val statusBg = PerformerStatusHandler.getStatusBg(status)

                    binding.tvStatus.text = statusText

                    binding.tvStatus.setBackgroundResource(statusBg)

                    val bustSize = context.getBustSize(consultant.bust)
                    if (bustSize == "") {
                        binding.tvSize.visibility = View.GONE
                    } else {
                        binding.tvSize.visibility = View.VISIBLE
                        binding.tvSize.text = bustSize
                    }
                    binding.tvAge.text = consultant.age.toString() + context.resources.getString(R.string.age_raw)
                    binding.tvName.text = consultant.name
                }
            }
            binding.ivBackgroundMain.setImageResource(when (typeRankingLayout) {
                BUNDLE_KEY.TYPE_DAILY -> R.drawable.ic_ranking_daily_3
                BUNDLE_KEY.TYPE_WEEKLY -> R.drawable.ic_ranking_week_3
                BUNDLE_KEY.TYPE_MONTHLY -> R.drawable.ic_ranking_monthly_3
                else -> R.drawable.ic_ranking_best_3
            })
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


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (typeRankingLayout == TYPE_RECOMMEND && (item.recommendRanking == 1 || item.recommendRanking == 2)) {
            RANKING_TOP_1_2
        } else if (item.ranking == 1 || item.ranking == 2) {
            RANKING_TOP_1_2
        } else {
            RANKING_TOP_3
        }
    }

    companion object {
        const val RANKING_TOP_1_2 = 12
        const val RANKING_TOP_3 = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RANKING_TOP_1_2 -> {
                RankingTop12Holder(
                    ItemRankingTop12Binding.inflate(layoutInflater, parent, false),
                )
            }
            else -> {
                RankingTop3Holder(
                    ItemRankingTop3Binding.inflate(layoutInflater, parent, false),
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RankingTopAdapter.RankingTop12Holder -> {
                getItem(position).let { holder.bind(it) }
                holder.binding.clRanking.requestLayout()
            }
            is RankingTopAdapter.RankingTop3Holder -> {
                getItem(position).let { holder.bind(it) }
                holder.binding.clRanking.requestLayout()
            }
        }
    }

}
