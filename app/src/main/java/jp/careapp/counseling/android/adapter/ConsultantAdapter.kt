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
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerRankingHandler
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatus
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.ItemConsultantBinding

class ConsultantAdapter(
    val context: Context,
    private val listener: (Int, List<ConsultantResponse>) -> Unit,
    private val isExitLiveStreamScreen: Boolean = false
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
            binding.ivRanking.loadImage(
                PerformerRankingHandler.getImageViewForRank(
                    consultant.ranking,
                    consultant.recommendRanking
                )
            )

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

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.text = statusText

            binding.clStatus.setBackgroundResource(statusBg)

            binding.ivStateBeginner.visibility = if(consultant.isRookie == 1) View.VISIBLE else View.GONE
            binding.ivRanking.visibility = if (isExitLiveStreamScreen) View.INVISIBLE else View.VISIBLE
            binding.tvLiveStreamCount.visibility = if (status == PerformerStatus.LIVE_STREAM) View.VISIBLE else View.GONE
            binding.tvLiveStreamCount.text = (consultant.loginMemberCount + consultant.peepingMemberCount).toString()
            val bustSize = context.getBustSize(consultant.bust)
            if (bustSize == "") {
                binding.tvSize.visibility = View.GONE
            } else {
                binding.tvSize.visibility = View.VISIBLE
                binding.tvSize.text = "(" + bustSize + ")"
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
