package jp.careapp.counseling.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.counseling.android.data.model.TutorialItem
import jp.careapp.counseling.databinding.ItemTutorialPageBinding

class TutorialViewPagerAdapter(
    private val context: Context,
    private val items: List<TutorialItem>
) : RecyclerView.Adapter<TutorialViewPagerAdapter.ViewPagerObjectViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewPagerObjectViewHolder, position: Int) =
        holder.bind(items[position])

    inner class ViewPagerObjectViewHolder(val binding: ItemTutorialPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TutorialItem) {
            with(binding) {
                binding.tvTitleTutorial.text = context.getString(item.titleId)
                binding.tvContentTutorial.text = HtmlCompat.fromHtml(
                    context.getString(item.contentId),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.ivLogo.setImageResource(item.imageResource)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerObjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTutorialPageBinding.inflate(inflater, parent, false)
        return ViewPagerObjectViewHolder(binding)
    }
}
