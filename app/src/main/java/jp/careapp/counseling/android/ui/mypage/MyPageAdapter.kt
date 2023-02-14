package jp.careapp.counseling.android.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.MyPageItem
import jp.careapp.counseling.databinding.ItemActionMypageBinding

class MyPageAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventAction
) : ListAdapter<MyPageItem, MyPageViewHolder>(
    MyPageDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageViewHolder {
        return MyPageViewHolder.from(
            parent,
            lifecycleOwner = lifecycleOwner,
            eventsAction = events
        )
    }

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class MyPageViewHolder(
    private val binding: ItemActionMypageBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val events: EventAction
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MyPageItem) {
        binding.apply {
            data = item
            event = events
            lifecycleOwner = this@MyPageViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            lifecycleOwner: LifecycleOwner,
            eventsAction: EventAction
        ): MyPageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemActionMypageBinding =
                ItemActionMypageBinding.inflate(layoutInflater, parent, false)
            return MyPageViewHolder(
                binding,
                lifecycleOwner,
                eventsAction
            )
        }
    }
}

interface EventAction {
    fun onclickItem(item: MyPageItem)
}

object MyPageDiffCallBack : DiffUtil.ItemCallback<MyPageItem>() {
    override fun areItemsTheSame(oldItem: MyPageItem, newItem: MyPageItem): Boolean =
        oldItem.checked == newItem.checked &&
            oldItem.icon == newItem.icon &&
            oldItem.destination == newItem.destination &&
            oldItem.isShowWarning == newItem.isShowWarning

    override fun areContentsTheSame(oldItem: MyPageItem, newItem: MyPageItem): Boolean =
        oldItem == newItem
}
