package jp.careapp.counseling.android.ui.rank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
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
    private var typeRanking: Int = 0

    private val viewModel: ListTypeRankingViewModel by viewModels()
    override val layoutId = R.layout.fragment_list_type_ranking
    override fun getVM() = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    private lateinit var ranking49Adapter: RankingBottomAdapter

    private lateinit var ranking1030Adapter: RankingBottomAdapter

    private lateinit var  ranking13Adapter: RankingTopAdapter

    override fun initView() {
        super.initView()
        arguments?.let {
            typeRanking = it.getInt(BUNDLE_KEY.TYPE_RANKING)
        }

        ranking13Adapter = RankingTopAdapter(
            requireContext(),
            onClickListener = { position ->
                if (!isDoubleClick) {
                    onClickDetailRanking(position)
                }
            },
            typeRanking
        )

        ranking49Adapter =
            RankingBottomAdapter(
                requireContext(),
                onClickListener = { position ->
                    if (!isDoubleClick) {
                        onClickDetailRanking(position+3)
                    }
                },
                typeRanking,
                2
            )

        ranking1030Adapter =
            RankingBottomAdapter(
                requireContext(),
                onClickListener = { position ->
                    if (!isDoubleClick) {
                        onClickDetailRanking(position+9)
                    }
                },
                typeRanking,
                3
            )



        binding.rvRanking49.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvRanking49.adapter = ranking49Adapter
        binding.rvRanking1030.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvRanking1030.adapter = ranking1030Adapter
        binding.rvRanking13.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvRanking13.adapter = ranking13Adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            if(typeRanking == BUNDLE_KEY.TYPE_RECOMMEND){
                activity?.let { act -> viewModel.getListRecommendRanking(act, true) }
            } else {
                activity?.let { act -> viewModel.getListTypeRanking(typeRanking, act, true) }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showTopRanking(listTop: List<TypeRankingResponse>) {
        if (!listTop.isNullOrEmpty()) {
            binding.tvNoRanking.visibility = View.GONE
            binding.rvRanking49.visibility = View.VISIBLE
            ranking13Adapter.submitList(listTop)
        } else {
            binding.rvRanking49.visibility = View.GONE
            binding.tvNoRanking.visibility = View.VISIBLE
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
                            if (it.size > 6) {
                                val ranking49List = it.subList(0, 6)
                                ranking49Adapter.submitList(ranking49List)
                                val ranking1030List = it.subList(6, it.size)
                                ranking1030Adapter.submitList(ranking1030List)
                            } else {
                                ranking49Adapter.submitList(it)
                            }
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
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(viewModel.getListConsultant())
        appNavigation.openRankingToUserProfileScreen(bundle)
    }

    fun scrollToTop() {
        if (isVisible) {
            binding.scrollViewRanking.smoothScrollTo(0, binding.scrollViewRanking.top)
        }
    }

    fun loadData(isShowLoading: Boolean) {
        if(typeRanking == BUNDLE_KEY.TYPE_RECOMMEND){
            activity?.let { act -> viewModel.getListRecommendRanking(act, isShowLoading) }
        } else {
            activity?.let { act -> viewModel.getListTypeRanking(typeRanking, act, isShowLoading) }
        }
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
