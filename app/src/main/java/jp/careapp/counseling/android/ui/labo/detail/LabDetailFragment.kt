package jp.careapp.counseling.android.ui.labo.detail

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_1
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_10
import jp.careapp.core.utils.DateUtil.Companion.convertStringToDateString
import jp.careapp.core.utils.DateUtil.Companion.getTimeRemaining
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.AnswerAdapter
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.adapter.OnItemAnswerClickListener
import jp.careapp.counseling.android.data.model.history_chat.Performer
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.network.AnswerResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.LabDetailResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.LabStatus.Companion.ACCEPTING_ANSWERS
import jp.careapp.counseling.android.utils.LabStatus.Companion.WAITING_BEST_ANSWERS
import jp.careapp.counseling.android.utils.LoadMoreState
import jp.careapp.counseling.android.utils.customView.ToolbarPoint
import jp.careapp.counseling.databinding.FragmentLabDetailBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LabDetailFragment :
    BaseFragment<FragmentLabDetailBinding, LabDetailViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences
    private val mViewModel: LabDetailViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    override val layoutId = R.layout.fragment_lab_detail
    override fun getVM() = mViewModel
    private lateinit var mAdapter: AnswerAdapter
    private lateinit var status: List<String>
    private var isFirstLoad = true
    private var lab: LaboResponse? = null

    override fun initView() {
        super.initView()
        initToolbar()
        initAdapter()
        setListeners()
        initData()
    }

    private fun initToolbar() {
        binding.toolBar.setPoint(
            String.format(
                getString(R.string.point),
                formatPoint.format(rxPreferences.getPoint())
            )
        )
    }

    private fun initAdapter() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvAnswer.layoutManager = layoutManager
        mAdapter = AnswerAdapter(
            requireContext(),
            object : OnItemAnswerClickListener {
                override fun onClickAnswer(answer: AnswerResponse) {

                }

                override fun onClickAnswerInfo(answer: AnswerResponse) {
                    if (!isDoubleClick && lab != null) {
                        openUserProfile(answer.performer)
                    }
                }

                override fun onClickChooseBestAnswer(answer: AnswerResponse) {
                    if (!isDoubleClick) {
                        confirmChooseBestAnswer(answer.id)
                    }
                }

                override fun onClickTalkToConsultant(answer: AnswerResponse) {
                    if (!isDoubleClick) {
                        openUserProfile(answer.performer)
                    }
                }

                override fun onClickReportLabo() {
                    if (!isDoubleClick) {
                        openReportLabo()
                    }
                }
            }
        )
        mAdapter.setLoadMorelistener(
            object : BaseAdapterLoadMore.LoadMorelistener {
                override fun onLoadMore() {
                    if (!mAdapter.disableLoadMore) {
                        mViewModel.loadLabDetail(true)
                    }
                }
            }
        )
        binding.rvAnswer.adapter = mAdapter
    }

    private fun openReportLabo() {
        Bundle().also {
            it.putInt(BUNDLE_KEY.LAB_ID, mViewModel.getLabId())
        }.let {
            appNavigation.openLabDetailToReportLaboScreen(it)
        }
    }

    private fun showGuideChooseBestAnswer() {
        isFirstLoad = false
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.choose_the_best_answer)
            .setContent(R.string.guide_choose_the_best_answer)
            .setTextPositiveButton(R.string.text_OK)
            .setOnPositivePressed {
                it.dismiss()
            }
    }

    private fun confirmChooseBestAnswer(answerId: Int) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.confirm_choose_best_answer)
            .setTextPositiveButton(R.string.make_it_best_answer)
            .setTextNegativeButton(R.string.reselect)
            .setOnPositivePressed {
                it.dismiss()
                mViewModel.chooseBestAnswer(answerId)
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun openUserProfile(performer: Performer) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
        bundle.putSerializable(
            BUNDLE_KEY.LIST_USER_PROFILE,
            ArrayList(listOf(ConsultantResponse.from(performer)))
        )
        appNavigation.openLabDetailToUserProfileScreen(bundle)
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            reloadData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.layoutQuestion.layoutFavourite.setOnClickListener {
            if (!isDoubleClick) {
                mViewModel.changeLabFavorite()
            }
        }

        binding.toolBar.setToolBarPointListener(
            object : ToolbarPoint.ToolBarPointListener() {
                override fun clickLeftBtn() {
                    super.clickLeftBtn()
                    if (!isDoubleClick) {
                        appNavigation.navigateUp()
                    }
                }

                override fun clickPointBtn() {
                    if (!isDoubleClick) {
                        appNavigation.openLabDetailToBuyPointScreen()
                    }
                }
            })

        binding.tvReport.setOnClickListener {
            if (!isDoubleClick) {
                openReportLabo()
            }
        }
    }

    private fun initData() {
        status = listOf(
            getString(R.string.accepting_answers),
            getString(R.string.waiting_for_the_best_answer),
            getString(R.string.solved)
        )
        arguments?.apply {
            var labId = -1
            if (containsKey(BUNDLE_KEY.LAB)) {
                lab = getSerializable(BUNDLE_KEY.LAB) as LaboResponse
                lab?.let {
                    setLabDetail(LabDetailResponse.from(it))
                    labId = it.id
                }
            } else if (containsKey(BUNDLE_KEY.LAB_ID)) {
                labId = getInt(BUNDLE_KEY.LAB_ID)
            }
            mViewModel.setLabId(labId)
            reloadData()
        }
    }

    fun reloadData() {
        mViewModel.resetDataLoadMore()
        mViewModel.loadLabDetail(false)
    }

    fun reloadData(labId: Int) {
        mViewModel.setLabId(labId)
        reloadData()
    }

    private fun setLabDetail(data: LabDetailResponse) {
        mAdapter.clearData()
        setDataQuestion(data)
        setDataAnswers(data.answers)
    }

    private fun setDataQuestion(data: LabDetailResponse) {
        val isOwner = data.member.code == rxPreferences.getMemberCode()
        mAdapter.isOwner = isOwner
        mAdapter.status = data.status

        if (isFirstLoad && isOwner && data.status == WAITING_BEST_ANSWERS) {
            showGuideChooseBestAnswer()
        }

        binding.layoutQuestion.apply {
            tvCategory.text = getCategory(data.genre)
            if (data.acceptAnswerEndAt.isNullOrEmpty()) {
                tvAcceptAnswerEnd.isVisible = false
            } else {
                val timeRemaining = getTimeRemaining(data.acceptAnswerEndAt)
                tvAcceptAnswerEnd.isVisible = !timeRemaining.isNullOrEmpty()
                tvAcceptAnswerEnd.text = getString(R.string.response_deadline, timeRemaining)
            }
            tvAcceptAnswerEnd.isVisible = data.status == ACCEPTING_ANSWERS
            if (status.size >= data.status && data.status - 1 >= 0) {
                tvStatus.text = status[data.status - 1]
                context?.let { tvStatus.setTextColor(it.getColor(getColorResourceStatus(data.status))) }
                tvStatus.background =
                    context?.getDrawable(getBackgroundResourceStatus(data.status))
            }
            tvContent.text = data.body
            tvName.text = data.member.counseleeName
            tvAge.text = getString(R.string.age_pattern, data.member.age)
            tvCreatedAt.text =
                convertStringToDateString(data.createdAt, DATE_FORMAT_1, DATE_FORMAT_10)
        }
        mViewModel.isFavorite = data.favoriteIsEnabled
        setFavorite(data.favoriteIsEnabled)
    }

    private fun getCategory(id: Int): String {
        val genres = rxPreferences.getListCategory() ?: return ""
        if (genres.isNotEmpty() && genres.any { it.id == id })
            return genres.filter { it.id == id }[0].name
        return ""
    }

    private fun getBackgroundResourceStatus(status: Int): Int {
        return when (status) {
            ACCEPTING_ANSWERS -> R.drawable.bg_corner_16dp_stroke_978dff
            WAITING_BEST_ANSWERS -> R.drawable.bg_corner_16dp_stroke_ff2875
            else -> R.drawable.bg_corner_16dp_stroke_aea2d1
        }
    }

    private fun getColorResourceStatus(status: Int): Int {
        return when (status) {
            ACCEPTING_ANSWERS -> R.color.color_978DFF
            WAITING_BEST_ANSWERS -> R.color.color_FF2875
            else -> R.color.color_AEA2D1
        }
    }

    private fun setFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            binding.layoutQuestion.apply {
                ivFavourite.setImageResource(R.drawable.ic_favourite)
                tvFavourite.text = getString(R.string.curious)
                layoutFavourite.background = context?.getDrawable(R.drawable.bg_corner_16dp_1d1045)
                tvFavourite.setTextAppearance(R.style.text_bold_12)
            }
        } else {
            binding.layoutQuestion.apply {
                ivFavourite.setImageResource(R.drawable.ic_un_favourite)
                tvFavourite.text = getString(R.string.concern)
                layoutFavourite.background =
                    context?.getDrawable(R.drawable.bg_corner_16dp_stroke_978dff)
                tvFavourite.setTextAppearance(R.style.text_12)
            }
        }
    }

    private fun setDataAnswers(it: List<AnswerResponse>) {
        mAdapter.run {
            val newData = ArrayList(it)
            if (it.isNotEmpty()) {
                newData.add(AnswerResponse(isItemReport = true))
            }
            submitList(newData)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        mViewModel.totalAnswerResult.observe(viewLifecycleOwner, handleTotalAnswerResult)
        mViewModel.labDetailResult.observe(viewLifecycleOwner, mHandleLabDetailResult)
        mViewModel.listAnswerResult.observe(viewLifecycleOwner, handleListAnswerResult)
        mViewModel.visibleLoadMoreResult.observe(viewLifecycleOwner, handleVisibleLoadMoreResult)
        mViewModel.enableLoadMoreResult.observe(viewLifecycleOwner, handleEnableLoadMoreResult)
        mViewModel.noDataResult.observe(viewLifecycleOwner, handleNoDataResult)
        mViewModel.chooseBestAnswerResult.observe(viewLifecycleOwner, handleChooseBestAnswerResult)
        mViewModel.checkFavoriteResult.observe(viewLifecycleOwner, handleCheckFavoriteResult)
    }

    private var handleTotalAnswerResult: Observer<Int> = Observer {
        it?.let {
            mAdapter.total = it
        }
    }

    private var mHandleLabDetailResult: Observer<LabDetailResponse?> = Observer {
        it?.let {
            setLabDetail(it)
        }
    }

    private var handleListAnswerResult: Observer<List<AnswerResponse>> = Observer {
        it?.let {
            setDataAnswers(it)
        }
    }

    private var handleVisibleLoadMoreResult: Observer<Int> = Observer {
        when (it) {
            LoadMoreState.SHOW_LOAD_MORE -> {
                if (!mAdapter.isLoading) {
                    mAdapter.setIsLoading(true)
                }
            }
            LoadMoreState.HIDDEN_LOAD_MORE -> {
                if (mAdapter.isLoading) {
                    mAdapter.setIsLoading(false)
                }
            }
        }
    }

    private var handleEnableLoadMoreResult: Observer<Int> = Observer {
        when (it) {
            LoadMoreState.DISABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(true)
            }
            LoadMoreState.ENABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(false)
            }
        }
    }

    private var handleNoDataResult: Observer<Boolean> = Observer { isNoData ->
        binding.tvNoData.isVisible = isNoData
        binding.tvLabelAnswer.isVisible = isNoData
        binding.tvReport.isVisible = isNoData
        binding.rvAnswer.isVisible = !isNoData
    }

    private var handleChooseBestAnswerResult: Observer<Boolean?> = Observer {
        if (true == it) {
            reloadData()
        }
    }

    private var handleCheckFavoriteResult: Observer<Boolean?> = Observer {
        it?.let {
            setFavorite(it)
        }
    }

    private val formatPoint: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale(","))
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3
        df
    }
}

