package jp.careapp.counseling.android.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.careapp.core.utils.DateUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.AnswerResponse
import jp.careapp.counseling.android.utils.LabStatus.Companion.DEFAULT
import jp.careapp.counseling.android.utils.LabStatus.Companion.SOLVED
import jp.careapp.counseling.android.utils.LabStatus.Companion.WAITING_BEST_ANSWERS
import jp.careapp.counseling.databinding.ItemBestAnswerBinding
import jp.careapp.counseling.databinding.ItemOtherAnswerBinding
import jp.careapp.counseling.databinding.ItemReportLaboBinding

class AnswerAdapter(
    val context: Context,
    val listener: OnItemAnswerClickListener
) : BaseAdapterLoadMore<AnswerResponse>(AnswerDiffUtil() as DiffUtil.ItemCallback<AnswerResponse>) {

    var isOwner = false
    var status = DEFAULT
    var total = 0

    companion object {
        const val VIEW_TYPE_BEST_ANSWER = 1
        const val VIEW_TYPE_OTHER_ANSWER = 2
        const val VIEW_TYPE_REPORT_LABO = 3
        const val MAX_LINE = 6
    }

    override fun onCreateViewHolderNormal(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_REPORT_LABO -> {
                ReportLaboHolder(ItemReportLaboBinding.inflate(inflater, parent, false), listener)
            }
            VIEW_TYPE_BEST_ANSWER -> {
                val binding = ItemBestAnswerBinding.inflate(inflater, parent, false)
                BestAnswerHolder(binding, listener)
            }
            else -> {
                val binding = ItemOtherAnswerBinding.inflate(inflater, parent, false)
                OtherAnswerHolder(binding, listener)
            }
        }
    }

    override fun onBinViewHolderNomal(holder: RecyclerView.ViewHolder, position: Int) {
        if (currentList[position].isItemReport) {
            (holder as ReportLaboHolder).bind()
        } else if (currentList[position].isBest) {
            (holder as BestAnswerHolder).bind(currentList[position])
        } else {
            (holder as OtherAnswerHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].isItemReport) {
            VIEW_TYPE_REPORT_LABO
        } else if (currentList[position].isBest) {
            VIEW_TYPE_BEST_ANSWER
        } else {
            VIEW_TYPE_OTHER_ANSWER
        }
    }

    fun clearData() {
        submitList(null)
    }

    private fun TextView.isEllipsized(): Boolean {
        layout?.apply {
            var ellipsisCount = getEllipsisCount(lineCount - 1)
            if (ellipsisCount > 0) {
                return true
            }
        }
        return false
    }

    private fun isFirstOtherAnswer(position: Int): Boolean {
        for (i in 0..position) {
            if (!currentList[i].isBest) {
                return i == position
            }
        }
        return false
    }

    private fun haveBestAnswer(): Boolean {
        for (answer in currentList) {
            if (answer.isBest) return true
        }
        return false
    }

    private fun countBestAnswer(): Int {
        var count = 0
        for (answer in currentList) {
            if (answer.isBest) {
                count += 1
            }
        }
        return count
    }

    inner class BestAnswerHolder(
        val binding: ItemBestAnswerBinding,
        val listener: OnItemAnswerClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: AnswerResponse) {
            binding.layoutAnswer.tvName.text = answer.performer.name
            binding.layoutAnswer.tvContent.text = answer.body
            binding.layoutAnswer.tvDate.text = DateUtil.convertStringToDateString(
                answer.createAt,
                DateUtil.DATE_FORMAT_1,
                DateUtil.DATE_FORMAT_10
            )
            Glide.with(context).load(answer.performer.thumbnailImageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_default)
                .into(binding.layoutAnswer.ivAvatar)

            binding.layoutAnswer.tvChooseBestAnswer.isVisible =
                isOwner && status == WAITING_BEST_ANSWERS
            binding.layoutAnswer.tvTalkToConsultant.isVisible = status == SOLVED

            if (status == SOLVED && answer.isBest) {
                expandContent()
            } else {
                collapseContent()
            }

            binding.layoutAnswer.clAnswerInfo.setOnClickListener {
                listener.onClickAnswerInfo(answer)
            }

            binding.layoutAnswer.tvContent.setOnClickListener {
                collapseContent()
            }

            binding.layoutAnswer.layoutContainer.setOnClickListener {
                listener.onClickAnswer(answer)
            }

            binding.layoutAnswer.tvChooseBestAnswer.setOnClickListener {
                listener.onClickChooseBestAnswer(answer)
            }

            binding.layoutAnswer.tvTalkToConsultant.setOnClickListener {
                listener.onClickTalkToConsultant(answer)
            }
        }

        private fun expandContent() {
            binding.layoutAnswer.tvContent.maxLines = Integer.MAX_VALUE
            binding.layoutAnswer.tvContent.ellipsize = null
            binding.layoutAnswer.tvSeeAll.isVisible = false
        }

        private fun collapseContent() {
            binding.layoutAnswer.tvContent.maxLines = MAX_LINE
            binding.layoutAnswer.tvContent.ellipsize = TextUtils.TruncateAt.END
            binding.layoutAnswer.tvContent.post {
                val isEllipsized = binding.layoutAnswer.tvContent.isEllipsized()
                binding.layoutAnswer.tvSeeAll.isVisible = isEllipsized
                if (isEllipsized) {
                    binding.layoutAnswer.tvSeeAll.setOnClickListener {
                        expandContent()
                    }
                }
            }
        }
    }

    inner class OtherAnswerHolder(
        val binding: ItemOtherAnswerBinding,
        val listener: OnItemAnswerClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: AnswerResponse) {
            binding.layoutType.isVisible = isFirstOtherAnswer(layoutPosition)
            if (haveBestAnswer()) {
                binding.tvLabelOtherAnswer.text = context.getString(R.string.other_answers)
                binding.tvCount.text = (total - countBestAnswer()).toString()
            } else {
                binding.tvLabelOtherAnswer.text = context.getString(R.string.answer)
                binding.tvCount.text = total.toString()
            }
            binding.layoutAnswer.tvName.text = answer.performer.name
            binding.layoutAnswer.tvContent.text = answer.body
            binding.layoutAnswer.tvDate.text = DateUtil.convertStringToDateString(
                answer.createAt,
                DateUtil.DATE_FORMAT_1,
                DateUtil.DATE_FORMAT_10
            )
            Glide.with(context).load(answer.performer.thumbnailImageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_default)
                .into(binding.layoutAnswer.ivAvatar)

            binding.layoutAnswer.tvChooseBestAnswer.isVisible =
                isOwner && status == WAITING_BEST_ANSWERS
            binding.layoutAnswer.tvTalkToConsultant.isVisible = status == SOLVED

            if (status == SOLVED && answer.isBest) {
                expandContent()
            } else {
                collapseContent()
            }

            binding.layoutAnswer.clAnswerInfo.setOnClickListener {
                listener.onClickAnswerInfo(answer)
            }

            binding.layoutAnswer.tvContent.setOnClickListener {
                collapseContent()
            }

            binding.layoutAnswer.layoutContainer.setOnClickListener {
                listener.onClickAnswer(answer)
            }

            binding.layoutAnswer.tvChooseBestAnswer.setOnClickListener {
                listener.onClickChooseBestAnswer(answer)
            }

            binding.layoutAnswer.tvTalkToConsultant.setOnClickListener {
                listener.onClickTalkToConsultant(answer)
            }
        }

        private fun expandContent() {
            binding.layoutAnswer.tvContent.maxLines = Integer.MAX_VALUE
            binding.layoutAnswer.tvContent.ellipsize = null
            binding.layoutAnswer.tvSeeAll.isVisible = false
        }

        private fun collapseContent() {
            binding.layoutAnswer.tvContent.maxLines = MAX_LINE
            binding.layoutAnswer.tvContent.ellipsize = TextUtils.TruncateAt.END
            binding.layoutAnswer.tvContent.post {
                val isEllipsized = binding.layoutAnswer.tvContent.isEllipsized()
                binding.layoutAnswer.tvSeeAll.isVisible = isEllipsized
                if (isEllipsized) {
                    binding.layoutAnswer.tvSeeAll.setOnClickListener {
                        expandContent()
                    }
                }
            }
        }
    }

    inner class ReportLaboHolder(
        val binding: ItemReportLaboBinding,
        val listener: OnItemAnswerClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvReport.setOnClickListener {
                listener.onClickReportLabo()
            }
        }
    }
}

class AnswerDiffUtil : DiffUtil.ItemCallback<AnswerResponse?>() {
    override fun areItemsTheSame(
        oldItem: AnswerResponse,
        newItem: AnswerResponse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AnswerResponse,
        newItem: AnswerResponse
    ): Boolean {
        return oldItem == newItem
    }
}

interface OnItemAnswerClickListener {
    fun onClickAnswer(answer: AnswerResponse)

    fun onClickAnswerInfo(answer: AnswerResponse)

    fun onClickChooseBestAnswer(answer: AnswerResponse)

    fun onClickTalkToConsultant(answer: AnswerResponse)

    fun onClickReportLabo()
}

