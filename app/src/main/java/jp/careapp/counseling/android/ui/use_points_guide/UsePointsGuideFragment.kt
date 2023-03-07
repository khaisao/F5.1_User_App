package jp.careapp.counseling.android.ui.use_points_guide

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentUsePointsGuideBinding
import javax.inject.Inject

@AndroidEntryPoint
class UsePointsGuideFragment :
    BaseFragment<FragmentUsePointsGuideBinding, UsePointsGuideViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_use_points_guide

    private val mViewModel: UsePointsGuideViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: UsePointsListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = UsePointsListAdapter()
        binding.rcvUsePointsGuideList.apply {
            adapter = mAdapter
            setHasFixedSize(true)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.usePointsGuideList.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    override fun onDestroyView() {
        binding.rcvUsePointsGuideList.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}