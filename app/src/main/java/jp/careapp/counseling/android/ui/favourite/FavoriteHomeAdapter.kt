package jp.careapp.counseling.android.ui.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.performer_extension.PerformerRankingHandler
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatus
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.ItemConsultantBinding

class FavoriteHomeAdapter(
    private val onItemClick: (item: FavoriteResponse) -> Unit
    ) : ListAdapter<FavoriteResponse, FavoriteHomeAdapter.FavoriteHomeViewModel>(
    FavoriteDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHomeViewModel {
        val binding = ItemConsultantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteHomeViewModel(binding)
    }

    override fun onBindViewHolder(holder: FavoriteHomeViewModel, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<FavoriteResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FavoriteHomeViewModel(
        private val binding: ItemConsultantBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(consultant: FavoriteResponse) {
            binding.tvName.text = consultant.name
            Glide.with(binding.root.context).load(consultant.thumbnailImageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.default_avt_performer)
                )
                .into(binding.ivAvatar)
            binding.rlConsultant.setOnClickListener {
                onItemClick(consultant)
            }

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, binding.root.context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.setBackgroundResource(statusBg)
            binding.tvStatus.text = statusText

            binding.ivStateBeginner.visibility = if(consultant.isRookie == 1) View.VISIBLE else View.GONE
            binding.tvLiveStreamCount.visibility = if (status == PerformerStatus.LIVE_STREAM) View.VISIBLE else View.GONE
            binding.tvLiveStreamCount.text = (consultant.loginMemberCount + consultant.peepingMemberCount).toString()
            val bustSize = binding.root.context.getBustSize(consultant.bust)
            if (bustSize == "") {
                binding.tvSize.visibility = View.GONE
            } else {
                binding.tvSize.visibility = View.VISIBLE
                binding.tvSize.text = "(" + bustSize + ")"
            }
            binding.tvAge.text =
                String.format(binding.root.context.getString(R.string.age_pattern), consultant.age)
            binding.ivRanking.setImageResource(
                PerformerRankingHandler.getImageViewForRank(
                    consultant.ranking,
                    consultant.recommendRanking
                )
            )
        }
    }
}


