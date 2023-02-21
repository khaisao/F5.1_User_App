package jp.careapp.counseling.android.ui.chatList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.data.model.history_chat.HistoryChatResponse
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileFragment
import jp.careapp.counseling.android.utils.HistoryMessage.PAID_MESS
import jp.careapp.counseling.databinding.ItemHistoryChatBinding

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
                            avatarIv.loadImage(it.imageUrl, R.drawable.ic_avatar_default, true)
                            if (it.presenceStatus == DetailUserProfileFragment.ACCEPTING) {
                                // TODO (Handle presence status)
                            } else {
                                // TODO (Handle presence status)
                            }
                            nameUserTv.text = it.name
                        } else {
                            Glide.with(avatarIv).load(
                                context.resources.getIdentifier(
                                    "thumb",
                                    "drawable", context.packageName
                                )
                            )
                                .circleCrop()
                                .into(avatarIv)
                            // TODO (Handle presence status)
                            nameUserTv.text =
                                context.resources.getString(R.string.notice_from_management)
                        }
                    }
                    lastMessageTv.text = data.body
                    // TODO (Handle rank - Waiting for design)
                    Glide.with(monthlyRank).load(
                        context.resources.getIdentifier(
                            "ic_daily_rank1",
                            "drawable", context.packageName
                        )
                    ).into(monthlyRank)
                    // TODO (Handle user cup - Waiting for API)
                    userCupTv.text = "Eカップ"
                    // TODO (Handle user age - Waiting for API)
                    userAgeTv.text = "30歳"
                    if (data.unreadCount > 0) {
                        statusOpenTv.text = data.unreadCount.toString()
                        statusOpenTv.visibility = VISIBLE
                    } else {
                        statusOpenTv.visibility = INVISIBLE
                    }
                    dateSentTv.text = DateUtil.getTimeHistoryChat(data.sendDate)

                    if (data.payOpen == PAID_MESS && (data.payFlag || data.open)) {
                        tvPayMessage.visibility = VISIBLE
                        lastMessageTv.visibility = GONE
                    } else {
                        tvPayMessage.visibility = GONE
                        lastMessageTv.visibility = VISIBLE
                    }
                }
                binding.root.setOnClickListener {
                    notifyItemChanged(adapterPosition)
                    onClickListener(data)
                }
            }
        }
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
