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
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.databinding.ItemConsultantBinding

class FavoriteHomeAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction,
) : ListAdapter<FavoriteResponse, FavoriteHomeViewModel>(
    FavoriteDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHomeViewModel {
        return FavoriteHomeViewModel.from(
            parent,
            lifecycleOwner = lifecycleOwner,
            eventsAction = events
        )
    }

    override fun onBindViewHolder(holder: FavoriteHomeViewModel, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun submitList(list: MutableList<FavoriteResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class FavoriteHomeViewModel(
    private val binding: ItemConsultantBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventFavoriteAction
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(consultant: FavoriteResponse) {
        binding.apply {
            lifecycleOwner = this@FavoriteHomeViewModel.lifecycleOwner
            executePendingBindings()
        }
        binding.tvName.text = consultant.name
        Glide.with(binding.root.context).load(consultant.thumbnailImageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.default_avt_performer)
            )
            .into(binding.ivAvatar)
        binding.rlConsultant.setOnClickListener {
            events.onclickItem(consultant)
        }
        if (ConsultantResponse.isWaiting(consultant.callStatus, consultant.chatStatus)) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_waiting)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_waiting)
        } else if (ConsultantResponse.isLiveStream(consultant.callStatus, consultant.chatStatus)) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_live_streaming)
        } else if (ConsultantResponse.isPrivateLiveStream(
                consultant.callStatus,
                consultant.chatStatus
            )
        ) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_private_delivery)
        } else {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
            binding.tvStatus.text =
                binding.root.context.resources.getString(R.string.presence_status_offline)
        }
        val bustSize = binding.root.context.getBustSize(consultant.bust)
        if (bustSize == "") {
            binding.tvSize.visibility = View.GONE
        } else {
            binding.tvSize.visibility = View.VISIBLE
            binding.tvSize.text = bustSize
        }
        binding.tvAge.text = String.format(binding.root.context.getString(R.string.age_pattern),consultant.age)

    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            eventsAction: EventFavoriteAction
        ): FavoriteHomeViewModel {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemConsultantBinding =
                ItemConsultantBinding.inflate(layoutInflater, parent, false)
            return FavoriteHomeViewModel(
                binding,
                lifecycleOwner,
                eventsAction
            )
        }
    }
}


