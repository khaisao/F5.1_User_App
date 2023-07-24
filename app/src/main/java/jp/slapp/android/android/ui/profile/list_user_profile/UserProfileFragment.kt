package jp.slapp.android.android.ui.profile.list_user_profile

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.data.model.user_profile.ActionLoadProfile
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.handle.HandleBuyPoint
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.slapp.android.android.ui.profile.block_report.BlockAndReportBottomFragment
import jp.slapp.android.android.ui.profile.detail_user.DetailUserProfileFragment
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.Define.Intent.Companion.OPEN_DIRECT_FROM_NOTIFICATION
import jp.slapp.android.android.utils.formatDecimalSeparator
import jp.slapp.android.databinding.FragmentUserProfileBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class UserProfileFragment :
    BaseFragment<FragmentUserProfileBinding, UserProfileViewModel>(), Serializable {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    override val layoutId = R.layout.fragment_user_profile

    private val viewModel: UserProfileViewModel by viewModels()

    override fun getVM(): UserProfileViewModel = viewModel

    private var isFromNotification = false

    private val shareViewModel: ShareViewModel by activityViewModels()

    // data screen
    private var pageSelected = 0
    private var performerCode = ""
    private var listUser: ArrayList<ConsultantResponse>? = null

    private var typeScreen = ""
    private var listFragment: MutableList<DetailUserProfileFragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatPoint(rxPreferences.getPoint())
        activity?.let { viewModel.loadMemberInfo(it) }
    }

    override fun initView() {
        super.initView()
        initAdapter()
        if (rxPreferences.isFirstShowGuideUser()) {
            binding.guideUserFr.visibility = VISIBLE
        }
        shareViewModel.isShowBuyPoint.observe(viewLifecycleOwner) {
            if (it) {
                listFragment[binding.userProfileVp.currentItem].doBuyPoint()
                shareViewModel.isShowBuyPoint.value = false
            }
        }

        binding.tvCurrentPoint.text = rxPreferences.getPoint().formatDecimalSeparator()
    }

    private fun initAdapter() {
        binding.userProfileVp.apply {
            offscreenPageLimit = 1
            adapter = UserProfileAdapter(listFragment, childFragmentManager)
        }
        binding.userProfileVp.currentItem = pageSelected
    }

    private fun initData() {
        val bundle = arguments
        listUser = ArrayList()
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_KEY.POSITION_SELECT))
                pageSelected = bundle.getInt(BUNDLE_KEY.POSITION_SELECT)
            if (bundle.containsKey(BUNDLE_KEY.LIST_USER_PROFILE)) {
                listUser =
                    bundle.getSerializable(BUNDLE_KEY.LIST_USER_PROFILE) as ArrayList<ConsultantResponse>
            } else {
                if (!listUser.isNullOrEmpty()) {
                    listUser!!.clear()
                }
                shareViewModel.getListPerformer()?.let { listUser!!.addAll(it) }
            }
            if (bundle.containsKey(OPEN_DIRECT_FROM_NOTIFICATION)) {
                isFromNotification = bundle.getBoolean(OPEN_DIRECT_FROM_NOTIFICATION, false)
            }
            typeScreen = bundle.getString(BUNDLE_KEY.SCREEN_TYPE, "")
            if (!listUser.isNullOrEmpty() && pageSelected != -1) {
                performerCode = listUser!![pageSelected].code.toString()
                shareViewModel.setPerformerCode(performerCode)
            }
        }
        if (!listUser.isNullOrEmpty()) {
            listUser?.let {
                for (i in 0 until it.size) {
                    listFragment.add(DetailUserProfileFragment.getInstance(i, it, typeScreen))
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.memberInFoResult.observe(viewLifecycleOwner, handleMemberResult)
        viewModel.blockUserResult.observe(viewLifecycleOwner, handleBlockResult)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPoint.collect {
                    binding.tvCurrentPoint.text = it
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMemberInfo()
    }

    private var handleMemberResult: Observer<MemberResponse?> = Observer {
        it?.let {
            formatPoint(it.point)
            shareViewModel.getCreditPrices(it.firstBuyCredit)
            rxPreferences.saveMemberInfo(it)
        }
    }

    private fun formatPoint(point: Int) {
        val symbols = DecimalFormatSymbols(Locale(","))
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3
    }

    private var handleBlockResult: Observer<Boolean> = Observer {
        if (it) {
            shareViewModel.isBlockConsultant.value = true
            // open from chat message
            if (typeScreen.equals(BUNDLE_KEY.CHAT_MESSAGE)) {
                appNavigation.popopBackStackToDetination(R.id.topFragment)
            } else {
                appNavigation.navigateUp()
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.userProfileVp.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    this@UserProfileFragment.pageSelected = position
                    if (!listUser.isNullOrEmpty()) {
                        performerCode = listUser!![pageSelected].code.toString()
                        shareViewModel.setPerformerCode(performerCode)
                    }
                }
            }
        )
        binding.guideUserFr.setOnClickListener {
            rxPreferences.saveFirstShowGuideUser(false)
            binding.guideUserFr.visibility = GONE
        }

        binding.ivBack.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.navigateUp()
            }
        }

        binding.llMemberPoint.setOnClickListener {
            handleBuyPoint.buyPoint(childFragmentManager, bundleOf(),
                object : BuyPointBottomFragment.HandleBuyPoint {
                    override fun buyPointSucess() {
                    }
                }
            )
        }
    }

    fun showBottomDialog(): Boolean = BlockAndReportBottomFragment.isShow

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionLoadProfile: ActionLoadProfile) {
        activity?.let { viewModel.loadMemberInfo(it) }
    }
}
