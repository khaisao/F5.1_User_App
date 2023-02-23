package jp.careapp.counseling.android.ui.review_mode.favorite_list

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.online_list.RMPerformerAdapter
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentRmFavoriteListBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMFavoriteListFragment :
    BaseFragment<FragmentRmFavoriteListBinding, RMFavoriteListViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: RMFavoriteListViewModel by viewModels()
    override fun getVM() = viewModel

    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_rm_favorite_list

    private val _adapter by lazy {
        RMPerformerAdapter(requireContext(),
            onClickListener = {
                if (!isDoubleClick) {
                    val bundle = Bundle().apply {
                        (it as? RMFavoriteResponse)?.code?.let {
                            putString(BUNDLE_KEY.PERFORMER_CODE, it)
                        }
                    }
                    appNavigation.openRMTopToRMUserDetail(bundle)
                }
            },
            onClickDelete = {
                (it as RMFavoriteResponse).code?.let { code ->
                    viewModel.deleteFavorite(code)
                }
            }
        )
    }

    override fun initView() {
        super.initView()

        binding.rvFavorite.apply {
            setHasFixedSize(true)
            adapter = _adapter
        }

        binding.srFavorite.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.getListFavorite()
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.listFavorite.observe(viewLifecycleOwner) {
            _adapter.submitList(it)
        }

        viewModel.iShowNoData.observe(viewLifecycleOwner) {
            binding.tvNoData.isVisible = it
        }
    }

    override fun onResume() {
        super.onResume()
        if (rmTopViewModel.isNeedUpdateData) {
            rmTopViewModel.isNeedUpdateData = false
            viewModel.getListFavorite()
        }
    }

    override fun onDestroyView() {
        binding.rvFavorite.adapter = null
        super.onDestroyView()
    }
}