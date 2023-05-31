package jp.careapp.counseling.android.ui.review_mode.online_list

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.LoadMoreState
import jp.careapp.counseling.databinding.FragmentRmOnlineListBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMOnlineListFragment : BaseFragment<FragmentRmOnlineListBinding, RMOnlineListViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: RMOnlineListViewModel by viewModels()
    override fun getVM() = viewModel
    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_rm_online_list

    private var _adapter: RMPerformerAdapter? = null

    override fun initView() {
        super.initView()

        _adapter = RMPerformerAdapter(
            onClickUser = {
                viewModel.handleOnClickUser(it)
            }
        ).apply {
            if (getLoadMorelistener() == null) {
                setLoadMorelistener(
                    object : BaseAdapterLoadMore.LoadMorelistener {
                        override fun onLoadMore() {
                            setIsLoading(true)
                            viewModel.getDummyPerformers(isLoadMore = true)
                        }
                    }
                )
            }
        }
        binding.rvOnline.apply {
            setHasFixedSize(true)
            adapter = _adapter
            addItemDecoration(SpacingItemDecorator(resources.getDimensionPixelSize(R.dimen.margin_8)))
        }

        binding.srOnline.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.clearData()
                viewModel.getDummyPerformers(true)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.onlineListLiveData.observe(viewLifecycleOwner) {
            _adapter?.submitList(it)
        }

        viewModel.loadMoreState.observe(viewLifecycleOwner) {
            it?.let {
                _adapter?.apply {
                    when (it) {
                        LoadMoreState.ENABLE_LOAD_MORE -> {
                            setDisableLoadmore(false)
                        }
                        LoadMoreState.DISABLE_LOAD_MORE -> {
                            setDisableLoadmore(true)
                        }
                        LoadMoreState.HIDDEN_LOAD_MORE -> {
                            if (isLoading) {
                                setIsLoading(false)
                            }
                        }
                    }
                }
            }
        }

        viewModel.isShowNoData.observe(viewLifecycleOwner) {
            binding.tvNoData.isVisible = it
        }

        viewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMOnlineListActionState.NavigateToUserDetail -> {
                    val bundle = bundleOf(BUNDLE_KEY.PERFORMER_CODE to it.userCode)
                    appNavigation.openRMTopToRMUserDetail(bundle)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (rmTopViewModel.isNeedUpdateData) {
            rmTopViewModel.isNeedUpdateData = false
            viewModel.getDummyPerformers(true)
        }
    }

    override fun onDestroyView() {
        binding.rvOnline.adapter = null
        _adapter = null
        super.onDestroyView()
    }
}