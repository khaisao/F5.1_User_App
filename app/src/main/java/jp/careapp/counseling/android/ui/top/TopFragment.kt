package jp.careapp.counseling.android.ui.top

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.main.MainActivity
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.MODE_USER
import jp.careapp.counseling.databinding.FragmentTopBinding
import me.leolin.shortcutbadger.ShortcutBadger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TopFragment : BaseFragment<FragmentTopBinding, TopViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val viewModel: TopViewModel by viewModels()

    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_top

    private var selectedItem: Int = 0
    private lateinit var navController: NavController

    override fun setOnClick() {
        super.setOnClick()
        binding.ivNavigationBack.setOnClickListener {
            binding.bottomNav.selectedItemId = R.id.tabHome
        }
        binding.tvPoint.setOnClickListener {
            if (!isDoubleClick) {
                if (viewModel.memberInFoResult.value?.disPlay == MODE_USER.MODE_ALL) {
                    val bundle = Bundle().apply {
                        putString(Define.TITLE_WEB_VIEW, getString(R.string.buy_point))
                        putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
                    }
                    appNavigation.openScreenToWebview(bundle)
                } else
                    appNavigation.openTopToBuyPointScreen()
            }
        }

        binding.ivSearch.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.openTopToSearchScreen()
            }
        }
        binding.ivNotification.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.openTopToNewsScreen()
            }
        }
    }

    override fun getVM(): TopViewModel = viewModel

    override fun initView() {
        super.initView()
        setupBottomNavigationBar()
        viewModel.loadMemberInfo()
        viewModel.loadUnreadCount()
    }

    override fun onResume() {
        super.onResume()
        try {
            val filter = IntentFilter(Define.Intent.ACTION_RECEIVE_NOTIFICATION)
            context?.registerReceiver(mReceiver, filter)
        } catch (e: Exception) {
            // do nothing
        }
        viewModel.loadUnreadCount()
    }

    override fun onPause() {
        super.onPause()
        try {
            context?.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            // do nothing
        }
    }

    private var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent.hasExtra(Define.Intent.DATA_FROM_NOTIFICATION)) {
                val bundle = intent.getBundleExtra(Define.Intent.DATA_FROM_NOTIFICATION)
                val data = bundle!!.getInt(Define.Intent.NUMBER_UNREAD_MESSAGE, 0)
                updateNumberUnread(data)
            }
        }
    }

    private fun updateNumberUnread(number: Int) {
        // update view when binding !=null
        if (!bindingIsNull()) {
            val viewBadge = getBottomBadge()
            viewBadge.isVisible = number > 0
            viewBadge.number = number
            try {
                ShortcutBadger.applyCount(context, number)
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private val bottomHeightListener = ViewTreeObserver.OnGlobalLayoutListener {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).heightToolbar = binding.bottomNav.height.toFloat()
        }
        removeListener()
    }

    private fun removeListener() {
        binding.bottomNav.viewTreeObserver.removeOnGlobalLayoutListener(bottomHeightListener)
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.viewTreeObserver.addOnGlobalLayoutListener(bottomHeightListener)
        bottomNavigationView.itemIconTintList = null

        navController =
            Navigation.findNavController(activity as Activity, R.id.top_nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)
        changeToolbarUI(bottomNavigationView.selectedItemId)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (isDoubleClick) {
                false
            }
            if (selectedItem != item.itemId) {
                onNavDestinationSelected(item, navController)
                shareViewModel.setTabSelected(getTabIndex(item.itemId))
            }
            true
        }
        bottomNavigationView.setOnNavigationItemReselectedListener {
            shareViewModel.setScrollToTop(true)
        }
    }

    private fun getTabIndex(bottomNavItemId: Int): Int {
        return when (bottomNavItemId) {
            R.id.tabHome -> ShareViewModel.TAB_HOME_SELECTED
            R.id.tabMessage -> ShareViewModel.TAB_CHAT_LIST_SELECTED
            R.id.tabRank -> ShareViewModel.TAB_RANKING_SELECTED
            R.id.tabFavorite -> ShareViewModel.TAB_FAVORITE_SELECTED
            R.id.tabMyPage -> ShareViewModel.TAB_MY_PAGE_SELECTED
            else -> -1
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.numberNotification.observe(
            viewLifecycleOwner,
            Observer {
                if (it.dataResponse.count > 0) {
                    binding.tvNotificationCount.visibility = VISIBLE
                    binding.tvNotificationCount.text = it.dataResponse.count.toString()
                } else {
                    binding.tvNotificationCount.visibility = GONE
                }
            }
        )
        viewModel.memberInFoResult.observe(viewLifecycleOwner, handleMemberResult)
        viewModel.unReadMessageCount.observe(viewLifecycleOwner, unreadMessageCount)
        shareViewModel.tabSelected.observe(viewLifecycleOwner, handleTabSelected)
        shareViewModel.valuePassFromMain.observe(viewLifecycleOwner, handleValueTransitionScreen)
    }

    fun getInstanceCurrentFragment(): CharSequence? {
        return navController.currentDestination!!.label
    }

    private var handleTabSelected: Observer<Int> = Observer {
        when (it) {
            ShareViewModel.TAB_HOME_SELECTED -> changeToolbarUI(R.id.tabHome)
            ShareViewModel.TAB_RANKING_SELECTED -> {
                binding.bottomNav.selectedItemId = R.id.tabRank
                changeToolbarUI(R.id.tabRank)
                shareViewModel.setTabSelected(-1)
            }
            ShareViewModel.TAB_CHAT_LIST_SELECTED -> {
                binding.bottomNav.selectedItemId = R.id.tabMessage
                changeToolbarUI(R.id.tabMessage)
            }
            ShareViewModel.TAB_MY_PAGE_SELECTED -> {
                binding.bottomNav.selectedItemId = R.id.tabMyPage
                changeToolbarUI(R.id.tabMyPage)
            }
            ShareViewModel.TAB_FAVORITE_SELECTED -> {
                binding.bottomNav.selectedItemId = R.id.tabFavorite
                changeToolbarUI(R.id.tabFavorite)
            }
        }
    }

    private var handleMemberResult: Observer<MemberResponse?> = Observer {
        it?.let {
            shareViewModel.getCreditPrices(it.firstBuyCredit)
            rxPreferences.saveMemberInfo(it)
            val symbols = DecimalFormatSymbols(Locale(","))
            val df = DecimalFormat()
            df.decimalFormatSymbols = symbols
            df.groupingSize = 3
            binding.tvPoint.text = String.format(getString(R.string.point), df.format(it.point))

            shareViewModel.needUpdateProfile.postValue(true)
        }
    }

    private var unreadMessageCount: Observer<Int?> = Observer {
        it?.let {
            updateNumberUnread(it)
        }
    }
    private var handleValueTransitionScreen: Observer<Int> = Observer {
        if (it != -1) {
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY.PERFORMER_CODE, it.toString())
            shareViewModel.valuePassFromMain.value = -1
            appNavigation.openTopToChatMessage(bundle)
        }
    }

    private fun changeToolbarUI(item: Int) {
        selectedItem = item
        when (item) {
            R.id.tabHome -> {
                binding.layoutActionbarHome.visibility = VISIBLE
                binding.layoutActionbarOtherTab.visibility = GONE
            }
            R.id.tabMessage, R.id.tabMyPage, R.id.tabRank, R.id.tabFavorite -> {
                binding.layoutActionbarHome.visibility = GONE
                binding.layoutActionbarOtherTab.visibility = VISIBLE
                when (item) {
                    R.id.tabRank -> {
                        binding.tvNavigationTitle.setText(R.string.tab_rank)
                        binding.ivNavigationBack.visibility = GONE
                    }
                    R.id.tabMessage -> {
                        binding.tvNavigationTitle.setText(R.string.tab_chat)
                        binding.ivNavigationBack.visibility = GONE
                    }
                    R.id.tabMyPage -> {
                        binding.tvNavigationTitle.setText(R.string.tab_my_page)
                        binding.ivNavigationBack.visibility = GONE
                    }
                    R.id.tabFavorite -> {
                        binding.tvNavigationTitle.setText(R.string.favorite_counselor)
                        binding.ivNavigationBack.visibility = GONE
                    }
                }
            }
            else -> {
                binding.bottomNav.visibility = GONE
                binding.toolbar.visibility = GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel.connectSocket()
        }
    }

    override fun onDestroy() {
        activity?.let {
            viewModel.closeSocket()
        }
        super.onDestroy()
    }

    fun getBottomBadge(): BadgeDrawable {
        val badge = binding.bottomNav.getOrCreateBadge(R.id.tabMessage)
        badge.maxCharacterCount = 3
        context?.let {
            badge.backgroundColor = ContextCompat.getColor(it, R.color.color_FF2875)
            badge.badgeTextColor = ContextCompat.getColor(it, R.color.white)
        }
        badge.verticalOffset = 14
        badge.horizontalOffset = 10
        badge.getPadding(Rect(2, 2, 2, 2))
        return badge
    }
}
