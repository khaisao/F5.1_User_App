package jp.careapp.counseling.android.ui.labo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_1
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_10
import jp.careapp.core.utils.DateUtil.Companion.convertStringToDateString
import jp.careapp.core.utils.executeAfter
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.LabStatus
import jp.careapp.counseling.databinding.ItemLaboBinding


class LaboAdapter(
    private val labEvent: LabEvent,
    private val context: Context,
    private val rxPreferences: RxPreferences
) : PagingDataAdapter<LaboResponse, LaboViewHolder>(LaboDiffCallBack) {

    override fun onBindViewHolder(holder: LaboViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it,context = context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaboViewHolder {
        return LaboViewHolder.from(parent, labEvent,rxPreferences)
    }
}

class LaboViewHolder(
    private val binding: ItemLaboBinding,
    private val labEvent: LabEvent,
    private val rxPreferences: RxPreferences
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: LaboResponse, context: Context) {
        binding.executeAfter {
            labo = item
            tvAge.text = " ${item.member.age}歳"
            tvStatus.text = changeStatus(item.status, context = context)
            tvCategory.text = getCategory(rxPreferences, item.genre)
            tvDate.text = convertStringToDateString(item.createdAt, DATE_FORMAT_1, DATE_FORMAT_10)
            tvTime.text = context.resources.getString(
                R.string.response_deadline,
                DateUtil.getTimeRemaining(item.acceptAnswerEndAt)
            )
            event = labEvent
            when (item.status) {
                LabStatus.ACCEPTING_ANSWERS -> {
                    tvStatus.apply {
                        setTextColor(context.resources.getColor(R.color.color_978DFF))
                        background =
                            (context.resources.getDrawable(R.drawable.bg_corner_16dp_stroke_978dff))
                    }
                }
                LabStatus.WAITING_BEST_ANSWERS -> {
                    tvStatus.apply {
                        setTextColor(context.resources.getColor(R.color.color_FF2875))
                        background =
                            (context.resources.getDrawable(R.drawable.bg_corner_16dp_stroke_ff2875))
                    }
                }
                else -> {
                    tvStatus.apply {
                        setTextColor(context.resources.getColor(R.color.color_AEA2D1))
                        background =
                            (context.resources.getDrawable(R.drawable.bg_corner_16dp_stroke_aea2d1))
                    }
                }
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup, labEvent: LabEvent,rxPreferences: RxPreferences): LaboViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLaboBinding.inflate(layoutInflater, parent, false)
            return LaboViewHolder(binding, labEvent,rxPreferences)
        }
    }
    fun getCategory(rxPreferences: RxPreferences,id:Int):String{
        val genres = rxPreferences.getListCategory() ?: return ""
        if (genres.isNotEmpty() && genres.any { it.id == id })
            return genres.filter { it.id == id }[0].name
        return ""
    }
}

interface LabEvent {
    fun labOnClick(lab: LaboResponse)
}

object LaboDiffCallBack : DiffUtil.ItemCallback<LaboResponse>() {

    override fun areItemsTheSame(oldItem: LaboResponse, newItem: LaboResponse): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: LaboResponse,
        newItem: LaboResponse
    ): Boolean = oldItem == newItem
}

fun changeStatus(status: Int,context: Context): String {
    if(status == 1) return context.resources.getString(R.string.accepting_answers)
    else if(status == 2) return context.resources.getString(R.string.waiting_for_the_best_answer)
    else return context.resources.getString(R.string.solved)
}
