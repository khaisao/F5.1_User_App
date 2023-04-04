package jp.careapp.counseling.android.ui.review_mode.privacy_policy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.LlItemRmPrivacyPolicyBinding

class RMPrivacyPolicyListAdapter :
    ListAdapter<RMPrivacyPolicyModelRecyclerView, RMPrivacyPolicyListAdapter.RMPrivacyPolicyListViewHolder>(
        RMPrivacyPolicyDiffUtil()
    ) {

    class RMPrivacyPolicyListViewHolder(private val binding: LlItemRmPrivacyPolicyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RMPrivacyPolicyModelRecyclerView) {
            if (item.title.isBlank()) {
                binding.tvTitle.visibility = View.GONE
            } else {
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.text = item.title
            }
            binding.tvContent.text = item.content
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RMPrivacyPolicyListViewHolder {
        return RMPrivacyPolicyListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.ll_item_rm_privacy_policy,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RMPrivacyPolicyListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RMPrivacyPolicyModelRecyclerView>?) {
        val result = arrayListOf<RMPrivacyPolicyModelRecyclerView>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }
}

class RMPrivacyPolicyDiffUtil : DiffUtil.ItemCallback<RMPrivacyPolicyModelRecyclerView>() {
    override fun areItemsTheSame(
        oldItem: RMPrivacyPolicyModelRecyclerView,
        newItem: RMPrivacyPolicyModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RMPrivacyPolicyModelRecyclerView,
        newItem: RMPrivacyPolicyModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }
}