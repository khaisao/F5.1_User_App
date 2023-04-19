package jp.careapp.counseling.android.ui.my_page

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentMypageBinding
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding, MyPageViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    override val layoutId: Int = R.layout.fragment_mypage

    private val mViewModel: MyPageViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: MyPageAdapter? = null

    override fun initView() {
        super.initView()

        mViewModel.getMemberInfo()

        mAdapter = MyPageAdapter { mViewModel.onClickItemMenu(it) }
        binding.rcvMyPageMenu.apply {
            adapter = mAdapter
            setHasFixedSize(true)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        binding.viewModel = mViewModel

        mViewModel.dataLiveData.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is MyPageActionState.NavigateToBuyPoints -> {
                    handleBuyPoint.buyPoint(childFragmentManager, bundleOf(),
                        object : BuyPointBottomFragment.HandleBuyPoint {
                            override fun buyPointSucess() {

                            }
                        }
                    )
                }
                is MyPageActionState.NavigateToEditProfile -> appNavigation.openMyPageToEditProfile()
                is MyPageActionState.NavigateToBlocked -> appNavigation.openMyPageToBlocked()
                is MyPageActionState.NavigateToSettingNotification -> appNavigation.openMyPageToNotification()
                is MyPageActionState.NavigateToUsePointsGuide -> appNavigation.openMyPageToUsePointsGuide()
                is MyPageActionState.NavigateToTermOfService -> appNavigation.openTermsOfService()
                is MyPageActionState.NavigateToPrivacyPolicy -> appNavigation.openPrivacyPolicy()
                is MyPageActionState.NavigateToFAQ -> appNavigation.openMyPageToFAQ()
                is MyPageActionState.NavigateToContactUs -> appNavigation.openContactUs()
            }
        }
    }

    override fun onDestroyView() {
        binding.rcvMyPageMenu.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}
