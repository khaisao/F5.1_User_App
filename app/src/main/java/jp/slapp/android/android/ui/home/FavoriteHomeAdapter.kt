package jp.slapp.android.android.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.data.network.FavoriteResponse
import jp.slapp.android.android.ui.favourite.FavoriteDiffCallBack
import jp.slapp.android.android.utils.extensions.getBustSize
import jp.slapp.android.android.utils.performer_extension.PerformerRankingHandler
import jp.slapp.android.android.utils.performer_extension.PerformerStatus
import jp.slapp.android.android.utils.performer_extension.PerformerStatusHandler
import jp.slapp.android.databinding.ItemConsultantBinding

class FavoriteHomeAdapter(
    val context: Context,
    private val listener: (Int, List<FavoriteResponse>) -> Unit
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

            binding.ivAvatar.loadImage(
                consultant.imageUrl,
                R.drawable.default_avt_performer
            )

            binding.rlConsultant.setOnClickListener {
                listener.invoke(absoluteAdapterPosition, currentList)
            }

            val status = PerformerStatusHandler.getStatus(consultant.callStatus,consultant.chatStatus)

            val statusText = PerformerStatusHandler.getStatusText(status, context.resources)

            val statusBg = PerformerStatusHandler.getStatusBg(status)

            binding.tvStatus.setBackgroundResource(statusBg)
            binding.tvStatus.text = statusText

            binding.ivStateBeginner.visibility = if(consultant.isRookie == 1) View.VISIBLE else View.GONE
            binding.tvLiveStreamCount.visibility = if (status == PerformerStatus.LIVE_STREAM) View.VISIBLE else View.GONE
            binding.tvLiveStreamCount.text = (consultant.loginMemberCount + consultant.peepingMemberCount).toString()
            val bustSize = context.getBustSize(consultant.bust)
            binding.tvSize.text = "(" + bustSize + ")"
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


