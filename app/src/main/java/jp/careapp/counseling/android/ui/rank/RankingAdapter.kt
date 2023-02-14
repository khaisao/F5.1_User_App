package jp.careapp.counseling.android.ui.rank

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.TypeRankingResponse
import jp.careapp.counseling.databinding.ItemRankingBinding

class RankingAdapter(
    val context: Context,
    private val onClickListener: (Int) -> Unit
) : ListAdapter<TypeRankingResponse, RankingAdapter.RankingHolder>((RankingDiffUtil())) {

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
            typeRanking?.let {
                binding.rankingNumber.text = context.getString(R.string.ranking_number, it.ranking)
                typeRanking.performerResponse?.let { it1 ->

                    Glide.with(context).load(it1.imageUrl)
                        .circleCrop()
                        .apply(RequestOptions().placeholder(R.drawable.ic_avatar_default))
                        .into(binding.ivAvatar)

                    if (it1.presenceStatus == 1) {
                        binding.tvPresenceStatus.text = context.getString(R.string.status_online)
                        binding.tvPresenceStatus.setBackgroundResource(R.drawable.ic_status_online)
                        binding.tvPresenceStatus.setTextColor(context.resources.getColor(R.color.color_1D1045))
                    } else {
                        binding.tvPresenceStatus.text = context.getString(R.string.status_offline)
                        binding.tvPresenceStatus.setBackgroundResource(R.drawable.ic_status_offline)
                        binding.tvPresenceStatus.setTextColor(context.resources.getColor(R.color.purple_AEA2D1))
                    }

                    binding.tvName.text = it1.name
                }
            }
            binding.clRanking.setOnClickListener {
                typeRanking?.let {
                    onClickListener.invoke(adapterPosition + 3)
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
