package jp.slapp.android.android.ui.use_points_guide

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.handle.HandleBuyPoint
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentUsePointsGuideBinding
import javax.inject.Inject

@AndroidEntryPoint
class UsePointsGuideFragment :
    BaseFragment<FragmentUsePointsGuideBinding, UsePointsGuideViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    override val layoutId: Int = R.layout.fragment_use_points_guide

    private val mViewModel: UsePointsGuideViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()
        setUpToolBar()
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnBuyPoints.setOnClickListener {
            if (!isDoubleClick) {
                handleBuyPoint.buyPoint(childFragmentManager, bundleOf(),
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {

                        }
                    }
                )
            }
        }
    }
}