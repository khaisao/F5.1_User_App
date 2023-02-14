package jp.careapp.counseling.android.ui.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.databinding.ItemBuyPointBinding
import jp.careapp.counseling.android.model.buy_point.ItemPoint

class CostPointAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: EventBuyPointAction
) : ListAdapter<ItemPoint, CostPointViewHolder>(
    MyPageDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostPointViewHolder {
        return CostPointViewHolder.from(
            parent,
            listener,
            lifecycleOwner = lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: CostPointViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class CostPointViewHolder(
    private val binding: ItemBuyPointBinding,
    private val listener: EventBuyPointAction,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ItemPoint) {
        binding.apply {
            data = item
            lifecycleOwner = this@CostPointViewHolder.lifecycleOwner
            executePendingBindings()
        }
        binding.actionBuyPointTv.setOnClickListener {
            listener.onclickItem(item)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: EventBuyPointAction,
            lifecycleOwner: LifecycleOwner
        ): CostPointViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemBuyPointBinding =
                ItemBuyPointBinding.inflate(layoutInflater, parent, false)
            return CostPointViewHolder(
                binding,
                listener,
                lifecycleOwner
            )
        }
    }
}

interface EventBuyPointAction {
    fun onclickItem(item: ItemPoint)
}

object MyPageDiffCallBack : DiffUtil.ItemCallback<ItemPoint>() {
    override fun areItemsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem.costFirst == newItem.costFirst

    override fun areContentsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem == newItem
}
