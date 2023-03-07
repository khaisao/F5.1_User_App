package jp.careapp.counseling.android.ui.my_page

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentMypageBinding
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding, MyPageViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_mypage

    private val mViewModel: MyPageViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: MyPageAdapter? = null

    override fun initView() {
        super.initView()

        mAdapter = MyPageAdapter { mViewModel.onClickItemMenu(it) }
        binding.rcvMyPageMenu.apply {
            adapter = mAdapter
            setHasFixedSize(true)
        }

//        viewModels.forceRefresh()
//        viewModels.uiMember.observe(
//            viewLifecycleOwner,
//            Observer {
//                it ?: return@Observer
//                binding.apply {
//                    member = it
//                    executePendingBindings()
//                }
//                if (it.disPlay == MODE_USER.MODE_ALL && it.firstBuyCredit) {
//                    binding.pointFree.visibility = View.VISIBLE
//                } else {
//                    binding.pointFree.visibility = View.GONE
//                }
//                binding.accountTransfer.isVisible = it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL
//                rxPreferences.setSignedUpStatus(it.signupStatus ?: SignedUpStatus.UNKNOWN)
//            }
//        )
//
//        viewModels.needUpdateTroubleSheetBadge.observe(viewLifecycleOwner, {
//            if (it) {
//                updateTroubleSheetMenu()
//            }
//        })
//
//        viewModels.memberLoading.observe(
//            viewLifecycleOwner,
//            Observer {
////                showHideLoading(it)
//            }
//        )
//        viewModels.uiNotification.observe(
//            viewLifecycleOwner,
//            Observer { noti ->
//                if (init) {
//                    if (noti.count != -1) {
//                        listItem.find { it.destination == Destination.NEWS }?.bage = noti.count
//                        myPageAdapter.submitList(listItem)
//                    }
//                } else {
//                    listItem.find { it.destination == Destination.NEWS }?.bage = 0
//                    myPageAdapter.submitList(listItem)
//                }
//            }
//        )
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    override fun bindingStateView() {
//        super.bindingStateView()
//        binding.apply {
//            viewModel = viewModels
//            executePendingBindings()
//        }
//        binding.pointFree.setOnClickListener {
//            if (!isDoubleClick) {
//                val bundle = Bundle().apply {
//                    putString(Define.TITLE_WEB_VIEW, getString(R.string.stripe_buy_point))
//                    putString(Define.URL_WEB_VIEW, Define.URL_STRIPE_BUY_POINT)
//                }
//                appNavigation.openScreenToWebview(bundle)
//            }
//        }
//        editProfileViewModel.updateSuccess.observe(
//            viewLifecycleOwner,
//            Observer {
//                it ?: return@Observer
//                binding.tvName.text = it
//            }
//        )
//        viewModels.navigateToEditProfileFragmentAction.observe(
//            viewLifecycleOwner,
//            EventObserver {
//                val bundle = Bundle().apply {
//                    putParcelable("member", it)
//                }
//                appNavigation.openMyPageToEditProfile(bundle)
//            }
//        )
//        val layoutManager = GridLayoutManager(requireContext(), 3)
//        layoutManager.isAutoMeasureEnabled = true
//        binding.rvAction.apply {
//            setHasFixedSize(false)
//            isNestedScrollingEnabled = false
//            this.layoutManager = layoutManager
//            adapter = myPageAdapter
//        }
//        viewModels.destination.observe(
//            viewLifecycleOwner,
//            EventObserver {
//                when (it) {
//                    Destination.SHEET -> {
//                        appNavigation.openMyPageToUpdateTroubleSheet(null)
//                    }
//                    Destination.LAB -> {
//                        appNavigation.openMyPageToLabScreen()
//                    }
//                    Destination.BUY_POINT -> {
//                        if (viewModels.uiMember.value?.disPlay == MODE_USER.MODE_ALL) {
//                            val bundle = Bundle().apply {
//                                putString(Define.TITLE_WEB_VIEW, getString(R.string.buy_point))
//                                putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
//                            }
//                            appNavigation.openScreenToWebview(bundle)
//                        } else
//                            appNavigation.openTopToBuyPointScreen()
//                    }
//                    Destination.NEWS -> {
//                        init = false
//                        appNavigation.openMyPageToNews()
//                    }
//                    Destination.ALERT -> {
//                        val bundle = Bundle().apply {
//                            putParcelable("member", viewModels.uiMember.value)
//                        }
//                        appNavigation.openMyPageToSettingNotification(bundle)
//                    }
//                    Destination.HELP -> {
//                        val bundle = Bundle().apply {
//                            putString(Define.TITLE_WEB_VIEW, getString(R.string.help))
//                            putString(Define.URL_WEB_VIEW, Define.URL_HELP)
//                        }
//                        appNavigation.openScreenToWebview(bundle)
//                    }
//                    Destination.SETTING -> {
//                        appNavigation.openMypageToSetting()
//                    }
//                    Destination.CONTACT -> {
//                        val bundle = Bundle().apply {
//                            putString("type_contact", this@MyPageFragment::class.java.simpleName)
//                        }
//                        appNavigation.openMyPageToContact(bundle)
//                    }
//                }
//            }
//        )
//        viewModels.memberMessage.observe(
//            viewLifecycleOwner,
//            EventObserver {
//            }
//        )
//        mainViewModels.currentFragment.observe(
//            viewLifecycleOwner,
//            Observer {
//                if (isVisible)
//                    viewModels.forceRefresh()
//                else
//                    return@Observer
//            }
//        )
//    }

//
//    private fun updateTroubleSheetMenu() {
//        if (troubleSheetBadgePosition != -1) {
//            context?.getDrawableCompat(R.drawable.ic_icomypagesheet)?.let {
//                listItem[troubleSheetBadgePosition] = MyPageItem(
//                    false,
//                    0,
//                    it,
//                    getString(R.string.sheet),
//                    Destination.SHEET,
//                    isShowWarning = !haveTroubleSheet
//                )
//            }
//            binding.rvAction.itemAnimator = null
//            myPageAdapter.notifyItemChanged(troubleSheetBadgePosition)
//        }
//    }

    override fun bindingStateView() {
        super.bindingStateView()

        binding.viewModel = mViewModel

        mViewModel.dataLiveData.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is MyPageActionState.NavigateToEditProfile -> appNavigation.openMyPageToEditProfile()
                is MyPageActionState.NavigateToBlocked -> appNavigation.openMyPageToBlocked()
                is MyPageActionState.NavigateToUsePointsGuide -> appNavigation.openMyPageToUsePointsGuide()
                is MyPageActionState.NavigateToTermOfService -> appNavigation.openMyPageToTermsOfService()
                is MyPageActionState.NavigateToPrivacyPolicy -> appNavigation.openMyPageToPrivacyPolicy()
            }
        }
    }

    override fun onDestroyView() {
        binding.rcvMyPageMenu.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}
