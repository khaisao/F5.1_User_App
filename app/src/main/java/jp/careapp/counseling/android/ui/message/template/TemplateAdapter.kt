package jp.careapp.counseling.android.ui.message.template

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FreeTemplateResponse
import jp.careapp.counseling.databinding.ItemTemplateBinding

class TemplateAdapter(
    context: Context,
    val onClickListener: (template: FreeTemplateResponse) -> Unit
) : ListAdapter<FreeTemplateResponse, TemplateAdapter.TemplateViewHolder>(DiffUtilCallback()) {

    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(
            ItemTemplateBinding.inflate(layoutInflater, parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    override fun submitList(list: List<FreeTemplateResponse>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class TemplateViewHolder(
        val binding: ItemTemplateBinding,
        private val onClickListener: (template: FreeTemplateResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(temp: FreeTemplateResponse) {
           binding.tvTemplate.text = temp.body
            binding.root.setOnClickListener {
                onClickListener.invoke(temp)
            }
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<FreeTemplateResponse>() {
    override fun areItemsTheSame(
        oldItem: FreeTemplateResponse,
        newItem: FreeTemplateResponse
    ): Boolean {
        return oldItem.id == oldItem.id
    }

    override fun areContentsTheSame(
        oldItem: FreeTemplateResponse,
        newItem: FreeTemplateResponse
    ): Boolean {

        return (oldItem.id == newItem.id) && (oldItem.body == newItem.body)
    }
}