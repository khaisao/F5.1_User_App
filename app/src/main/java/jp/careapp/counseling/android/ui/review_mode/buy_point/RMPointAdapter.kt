package jp.careapp.counseling.android.ui.review_mode.buy_point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.model.buy_point.ItemPoint
import jp.careapp.counseling.databinding.ItemRmBuyPointBinding

class RMPointAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: RMEventBuyPointAction
) : ListAdapter<ItemPoint, RMPointViewHolder>(
    MyPageDiffCallBack
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RMPointViewHolder {
        return RMPointViewHolder.from(
            parent,
            listener,
            lifecycleOwner = lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: RMPointViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class RMPointViewHolder(
    private val binding: ItemRmBuyPointBinding,
    private val listener: RMEventBuyPointAction,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ItemPoint) {
        binding.apply {
            data = item
            lifecycleOwner = this@RMPointViewHolder.lifecycleOwner
            executePendingBindings()
        }
        binding.btnBuyPoint.setOnClickListener {
            listener.onclickItem(item)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: RMEventBuyPointAction,
            lifecycleOwner: LifecycleOwner
        ): RMPointViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemRmBuyPointBinding =
                ItemRmBuyPointBinding.inflate(layoutInflater, parent, false)
            return RMPointViewHolder(
                binding,
                listener,
                lifecycleOwner
            )
        }
    }
}

interface RMEventBuyPointAction {
    fun onclickItem(item: ItemPoint)
}

object MyPageDiffCallBack : DiffUtil.ItemCallback<ItemPoint>() {
    override fun areItemsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem.costFirst == newItem.costFirst

    override fun areContentsTheSame(oldItem: ItemPoint, newItem: ItemPoint): Boolean =
        oldItem == newItem
}
