package jp.slapp.android.android.ui.privacy_policy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.slapp.android.R
import jp.slapp.android.databinding.LlItemPrivacyPolicyBinding

class PrivacyPolicyListAdapter :
    ListAdapter<PrivacyPolicyModelRecyclerView, PrivacyPolicyListAdapter.PrivacyPolicyListViewHolder>(
        PrivacyPolicyDiffUtil()
    ) {

    class PrivacyPolicyListViewHolder(private val binding: LlItemPrivacyPolicyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PrivacyPolicyModelRecyclerView) {
            if (item.title.isBlank()) {
                binding.tvTitle.visibility = View.GONE
            } else {
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.text = item.title
            }
            binding.tvContent.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyPolicyListViewHolder {
        return PrivacyPolicyListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.ll_item_privacy_policy,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PrivacyPolicyListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<PrivacyPolicyModelRecyclerView>?) {
        val result = arrayListOf<PrivacyPolicyModelRecyclerView>()
        list?.forEach { result.add(it.copy()) }
        super.submitList(result)
    }
}

class PrivacyPolicyDiffUtil : DiffUtil.ItemCallback<PrivacyPolicyModelRecyclerView>() {
    override fun areItemsTheSame(
        oldItem: PrivacyPolicyModelRecyclerView,
        newItem: PrivacyPolicyModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PrivacyPolicyModelRecyclerView,
        newItem: PrivacyPolicyModelRecyclerView
    ): Boolean {
        return oldItem == newItem
    }
}