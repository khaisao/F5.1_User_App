package jp.careapp.counseling.android.ui.rank

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.TypeRankingResponse
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentListTypeRankingBinding
import javax.inject.Inject

@AndroidEntryPoint
class ListTypeRankingFragment :
    BaseFragment<FragmentListTypeRankingBinding, ListTypeRankingViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private var typeRanking: Int? = 0

    private val viewModel: ListTypeRankingViewModel by viewModels()
    override val layoutId = R.layout.fragment_list_type_ranking
    override fun getVM() = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    private val mTypeRankingAdapter: RankingAdapter by lazy {
        RankingAdapter(
            requireContext(),
            onClickListener = { position ->
                if (!isDoubleClick) {
                    onClickDetailRanking(position)
                }
            }
        )
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            typeRanking = it.getInt(BUNDLE_KEY.TYPE_RANKING)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvRanking.layoutManager = layoutManager
        binding.rvRanking.adapter = mTypeRankingAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            activity?.let { act -> viewModel.getListTypeRanking(typeRanking!!, act, true) }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showBannerTypeRanking(type: Int) {
        when (type) {
            0 -> binding.llTopRanking.setBackgroundResource(R.drawable.bg_ranking_daily)
            1 -> binding.llTopRanking.setBackgroundResource(R.drawable.bg_ranking_weekly)
            2 -> binding.llTopRanking.setBackgroundResource(R.drawable.bg_ranking_monthly)
        }
    }

    private fun showTopRanking(listTop: List<TypeRankingResponse>) {
        if (!listTop.isNullOrEmpty()) {
            showBannerTypeRanking(typeRanking!!)
            binding.tvNoRanking.visibility = View.GONE
            binding.llTopRanking.visibility = View.VISIBLE
            binding.rvRanking.visibility = View.VISIBLE
            when (listTop.size) {
                1 -> {
                    listTop[0].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank1,
                            binding.nameRank1,
                            binding.statusRank1,
                            rankClickable = binding.rlRanking1
                        )
                    }
                    showInforPerformer(
                        ivAvatar = binding.avatarRank2,
                        tvName = binding.nameRank2,
                        tvStatus = binding.statusRank2,
                        isShow = false,
                        rankClickable = binding.rlRanking2
                    )
                    showInforPerformer(
                        ivAvatar = binding.avatarRank3,
                        tvName = binding.nameRank3,
                        tvStatus = binding.statusRank3,
                        isShow = false,
                        rankClickable = binding.rlRanking3
                    )
                }
                2 -> {
                    listTop[0].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank1,
                            binding.nameRank1,
                            binding.statusRank1,
                            rankClickable = binding.rlRanking1
                        )
                    }
                    listTop[1].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank2,
                            binding.nameRank2,
                            binding.statusRank2,
                            rankClickable = binding.rlRanking2
                        )
                    }
                    showInforPerformer(
                        ivAvatar = binding.avatarRank3,
                        tvName = binding.nameRank3,
                        tvStatus = binding.statusRank3,
                        isShow = false,
                        rankClickable = binding.rlRanking3
                    )
                }
                3 -> {
                    listTop[0].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank1,
                            binding.nameRank1,
                            binding.statusRank1,
                            rankClickable = binding.rlRanking1
                        )
                    }
                    listTop[1].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank2,
                            binding.nameRank2,
                            binding.statusRank2,
                            rankClickable = binding.rlRanking2
                        )
                    }
                    listTop[2].performerResponse?.let {
                        showInforPerformer(
                            it,
                            binding.avatarRank3,
                            binding.nameRank3,
                            binding.statusRank3,
                            rankClickable = binding.rlRanking3
                        )
                    }
                }
            }
        } else {
            binding.llTopRanking.visibility = View.GONE
            binding.rvRanking.visibility = View.GONE
            binding.tvNoRanking.visibility = View.VISIBLE
        }
    }

    private fun showInforPerformer(
        performerResponse: ConsultantResponse? = null,
        ivAvatar: AppCompatImageView,
        tvName: AppCompatTextView,
        tvStatus: AppCompatTextView,
        isShow: Boolean = true,
        rankClickable: RelativeLayout
    ) {
        rankClickable.isClickable = isShow
        if (!isShow) {
            tvName.visibility = View.GONE
            tvStatus.visibility = View.GONE
            return
        }
        tvName.visibility = View.VISIBLE
        tvStatus.visibility = View.VISIBLE


        context?.let {
            Glide.with(it).load(performerResponse?.imageUrl)
                .circleCrop()
                .apply(RequestOptions().placeholder(R.drawable.ic_avatar_default))
                .into(ivAvatar)
        }
        tvName.text = performerResponse?.name
        if (performerResponse?.presenceStatus == 1) {
            tvStatus.text = getString(R.string.status_online)
            tvStatus.setBackgroundResource(R.drawable.ic_status_online)
            tvStatus.setTextColor(resources.getColor(R.color.color_1D1045))
        } else {
            tvStatus.text = getString(R.string.status_offline)
            tvStatus.setBackgroundResource(R.drawable.ic_status_offline)
            tvStatus.setTextColor(resources.getColor(R.color.purple_AEA2D1))
        }

    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isReadyShowResult.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.rankingTopResult.observe(
                        viewLifecycleOwner,
                        Observer {
                            showTopRanking(it)
                        }
                    )
                    viewModel.listRankingResult.observe(
                        viewLifecycleOwner,
                        Observer {
                            mTypeRankingAdapter.submitList(it)
                        }
                    )
                }
            }
        )
        viewModel.isRankShowLoading.observe(viewLifecycleOwner) {
            shareViewModel.setRankLoading(it)
        }
    }

    private fun onClickDetailRanking(position: Int) {
        var bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(viewModel.getListConsultant())
        appNavigation.openRankingToUserProfileScreen(bundle)
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.rlRanking1.setOnClickListener {
            if (!isDoubleClick) {
                onClickDetailRanking(0)
            }
        }

        binding.rlRanking2.setOnClickListener {
            if (!isDoubleClick) {
                onClickDetailRanking(1)
            }
        }

        binding.rlRanking3.setOnClickListener {
            if (!isDoubleClick) {
                onClickDetailRanking(2)
            }
        }
    }

    fun scrollToTop() {
        if (isVisible) {
            binding.scrollViewRanking.smoothScrollTo(0, binding.scrollViewRanking.top)
        }
    }

    fun loadData(isShowLoading: Boolean) {
        activity?.let { act -> viewModel.getListTypeRanking(typeRanking!!, act, isShowLoading) }
    }

    companion object {
        @JvmStatic
        fun newInstance(typeRanking: Int) =
            ListTypeRankingFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_KEY.TYPE_RANKING, typeRanking)
                }
            }
    }
}
