package jp.careapp.counseling.android.ui.review_mode.online_list

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.model.network.RMPerformerResponse
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

    private val _adapter by lazy {
        RMPerformerAdapter(
            requireContext(),
            onClickListener = {
                if (!isDoubleClick) {
                    val bundle = Bundle().apply {
                        putString(BUNDLE_KEY.PERFORMER_CODE, (it as RMPerformerResponse).code)
                    }
                    appNavigation.openRMTopToRMUserDetail(bundle)
                }
            }
        ) {}.apply {
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
    }

    override fun initView() {
        super.initView()

        binding.rvOnline.apply {
            itemAnimator = null
            setHasFixedSize(true)
            adapter = _adapter
        }

        binding.srOnline.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.getDummyPerformers(true)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.listPerformers.observe(viewLifecycleOwner) {
            it?.let {
                _adapter.submitList(it)
            }
        }

        viewModel.loadMoreState.observe(viewLifecycleOwner) {
            it?.let {
                _adapter.apply {
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

        viewModel.iShowNoData.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvNoData.isVisible = it
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
        super.onDestroyView()
    }
}