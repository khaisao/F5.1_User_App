package jp.careapp.counseling.android.ui.review_mode.favorite_list

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentRmFavoriteListBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMFavoriteListFragment :
    BaseFragment<FragmentRmFavoriteListBinding, RMFavoriteListViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val mViewModel: RMFavoriteListViewModel by viewModels()
    override fun getVM() = mViewModel

    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_rm_favorite_list

    private var mAdapter: RMFavoriteListAdapter? = null

    override fun initView() {
        super.initView()

        mAdapter = RMFavoriteListAdapter(onClickUser = {
            mViewModel.handleOnClickUser(it)
        }, onClickDelete = {
            mViewModel.deleteFavorite(it)
        })

        binding.rvFavorite.apply {
            setHasFixedSize(true)
            adapter = mAdapter
        }

        binding.srFavorite.apply {
            setOnRefreshListener {
                isRefreshing = false
                mViewModel.getListFavorite()
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.favoriteListLiveData.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }

        mViewModel.isShowNoData.observe(viewLifecycleOwner) {
            binding.tvNoData.isVisible = it
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMFavoriteListActionState.NavigateToUserDetail -> {
                    val bundle = Bundle().apply {
                        putString(BUNDLE_KEY.PERFORMER_CODE, it.userCode)
                    }
                    appNavigation.openRMTopToRMUserDetail(bundle)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (rmTopViewModel.isNeedUpdateData) {
            rmTopViewModel.isNeedUpdateData = false
            mViewModel.getListFavorite()
        }
    }

    override fun onDestroyView() {
        binding.rvFavorite.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}