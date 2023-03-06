package jp.careapp.counseling.android.ui.mypage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.getDrawableCompat
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.MyPageItem
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.edit_profile.EditProfileViewModel
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.MODE_USER
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.android.utils.event.EventObserver
import jp.careapp.counseling.databinding.FragmentMypageBinding
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding, MyPageViewModel>() {

    private lateinit var myPageAdapter: MyPageAdapter1
    override val layoutId = R.layout.fragment_mypage
    private val viewModels: MyPageViewModel by activityViewModels()
    private val mainViewModels: MainViewModel by activityViewModels()
    private val editProfileViewModel: EditProfileViewModel by activityViewModels()
    val listItem: MutableList<MyPageItem> = mutableListOf()
    override fun getVM(): MyPageViewModel = viewModels
    var init: Boolean = true
    private var troubleSheetBadgePosition = -1

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val haveTroubleSheet: Boolean
        get() = viewModels.uiMember.value?.isHaveTroubleSheet ?: true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addItem()
        init = true
    }

    override fun initView() {
        super.initView()
        myPageAdapter = MyPageAdapter1(
            viewLifecycleOwner,
            viewModels
        )
        viewModels.forceRefresh()
        viewModels.uiMember.observe(
            viewLifecycleOwner,
            Observer {
                it ?: return@Observer
                binding.apply {
                    member = it
                    executePendingBindings()
                }
                if (it.disPlay == MODE_USER.MODE_ALL && it.firstBuyCredit) {
                    binding.pointFree.visibility = View.VISIBLE
                } else {
                    binding.pointFree.visibility = View.GONE
                }
                binding.accountTransfer.isVisible = it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL
                rxPreferences.setSignedUpStatus(it.signupStatus ?: SignedUpStatus.UNKNOWN)
            }
        )

        viewModels.needUpdateTroubleSheetBadge.observe(viewLifecycleOwner, {
            if (it) {
                updateTroubleSheetMenu()
            }
        })

        viewModels.memberLoading.observe(
            viewLifecycleOwner,
            Observer {
//                showHideLoading(it)
            }
        )
        viewModels.uiNotification.observe(
            viewLifecycleOwner,
            Observer { noti ->
                if (init) {
                    if (noti.count != -1) {
                        listItem.find { it.destination == Destination.NEWS }?.bage = noti.count
                        myPageAdapter.submitList(listItem)
                    }
                } else {
                    listItem.find { it.destination == Destination.NEWS }?.bage = 0
                    myPageAdapter.submitList(listItem)
                }
            }
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun bindingStateView() {
        super.bindingStateView()
        binding.apply {
            viewModel = viewModels
            executePendingBindings()
        }
        binding.pointFree.setOnClickListener {
            if (!isDoubleClick) {
                val bundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.stripe_buy_point))
                    putString(Define.URL_WEB_VIEW, Define.URL_STRIPE_BUY_POINT)
                }
                appNavigation.openScreenToWebview(bundle)
            }
        }
        editProfileViewModel.updateSuccess.observe(
            viewLifecycleOwner,
            Observer {
                it ?: return@Observer
                binding.tvName.text = it
            }
        )
        viewModels.navigateToEditProfileFragmentAction.observe(
            viewLifecycleOwner,
            EventObserver {
                val bundle = Bundle().apply {
                    putParcelable("member", it)
                }
                appNavigation.openMyPageToEditProfile(bundle)
            }
        )
        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.isAutoMeasureEnabled = true
        binding.rvAction.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            this.layoutManager = layoutManager
            adapter = myPageAdapter
        }
        viewModels.destination.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    Destination.SHEET -> {
                        appNavigation.openMyPageToUpdateTroubleSheet(null)
                    }
                    Destination.LAB -> {
                        appNavigation.openMyPageToLabScreen()
                    }
                    Destination.BUY_POINT -> {
                        if (viewModels.uiMember.value?.disPlay == MODE_USER.MODE_ALL) {
                            val bundle = Bundle().apply {
                                putString(Define.TITLE_WEB_VIEW, getString(R.string.buy_point))
                                putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
                            }
                            appNavigation.openScreenToWebview(bundle)
                        } else
                            appNavigation.openTopToBuyPointScreen()
                    }
                    Destination.NEWS -> {
                        init = false
                        appNavigation.openMyPageToNews()
                    }
                    Destination.ALERT -> {
                        val bundle = Bundle().apply {
                            putParcelable("member", viewModels.uiMember.value)
                        }
                        appNavigation.openMyPageToSettingNotification(bundle)
                    }
                    Destination.HELP -> {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.help))
                            putString(Define.URL_WEB_VIEW, Define.URL_HELP)
                        }
                        appNavigation.openScreenToWebview(bundle)
                    }
                    Destination.SETTING -> {
                        appNavigation.openMypageToSetting()
                    }
                    Destination.CONTACT -> {
                        val bundle = Bundle().apply {
                            putString("type_contact", this@MyPageFragment::class.java.simpleName)
                        }
                        appNavigation.openMyPageToContact(bundle)
                    }
                }
            }
        )
        viewModels.memberMessage.observe(
            viewLifecycleOwner,
            EventObserver {
            }
        )
        mainViewModels.currentFragment.observe(
            viewLifecycleOwner,
            Observer {
                if (isVisible)
                    viewModels.forceRefresh()
                else
                    return@Observer
            }
        )
    }

    private fun addItem() {
        listItem.clear()
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagesheet),
                getString(R.string.sheet),
                Destination.SHEET,
                isShowWarning = !haveTroubleSheet
            ),
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_my_page_labo),
                getString(R.string.tab_lab),
                Destination.LAB
            ),
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagebuypoint),
                getString(R.string.buy_point),
                Destination.BUY_POINT
            ),
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagenews),
                getString(R.string.news),
                Destination.NEWS
            )
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagealert),
                getString(R.string.alert),
                Destination.ALERT
            )
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagehelp),
                getString(R.string.help),
                Destination.HELP
            )
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagesetting),
                getString(R.string.setting),
                Destination.SETTING
            )
        )
        listItem.add(
            MyPageItem(
                false,
                0,
                resources.getDrawable(R.drawable.ic_icomypagecontact),
                getString(R.string.contact),
                Destination.CONTACT
            )
        )

        troubleSheetBadgePosition =
            listItem.indexOfFirst { myPageItem -> myPageItem.destination == Destination.SHEET }
    }

    private fun updateTroubleSheetMenu() {
        if (troubleSheetBadgePosition != -1) {
            context?.getDrawableCompat(R.drawable.ic_icomypagesheet)?.let {
                listItem[troubleSheetBadgePosition] = MyPageItem(
                    false,
                    0,
                    it,
                    getString(R.string.sheet),
                    Destination.SHEET,
                    isShowWarning = !haveTroubleSheet
                )
            }
            binding.rvAction.itemAnimator = null
            myPageAdapter.notifyItemChanged(troubleSheetBadgePosition)
        }
    }
}
