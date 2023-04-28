package jp.careapp.counseling.android.ui.main

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.dialog.LoadingDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.pref.AppPreferences
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.BuyPointFragment
import jp.careapp.counseling.android.ui.message.ChatMessageFragment
import jp.careapp.counseling.android.ui.news.NewsFragment
import jp.careapp.counseling.android.ui.profile.list_user_profile.UserProfileFragment
import jp.careapp.counseling.android.ui.registration.RegistrationFragment
import jp.careapp.counseling.android.ui.splash.SplashFragment
import jp.careapp.counseling.android.ui.top.TopFragment
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeFragment
import jp.careapp.counseling.android.utils.*
import jp.careapp.counseling.android.utils.Define.Companion.PREFIX_CARE_APP
import jp.careapp.counseling.android.utils.event.NetworkEvent
import jp.careapp.counseling.android.utils.event.NetworkState
import jp.careapp.counseling.android.utils.extensions.dismissAllDialog
import jp.careapp.counseling.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var networkEvent: NetworkEvent

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    @Inject
    lateinit var rxPreferences: RxPreferences

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel: MainViewModel by viewModels()
    private val shareViewModel: ShareViewModel by viewModels()

    private val listNotificationView: List<NotificationView> by lazy {
        listOf(
            NotificationView(
                StateView.HIDE,
                binding.notificationView1,
                binding.tvContentView1,
                binding.ivCloseView1
            ),
            NotificationView(
                StateView.HIDE,
                binding.notificationView2,
                binding.tvContentView2,
                binding.ivCloseView2
            ),
            NotificationView(
                StateView.HIDE,
                binding.notificationView3,
                binding.tvContentView3,
                binding.ivCloseView3
            )
        )
    }

    override fun getVM() = viewModel
    override val layoutId = R.layout.activity_main
    private lateinit var navHostFragment: NavHostFragment

    private var listNotiNewMessage = mutableListOf<SocketActionSend>()
    private var currentList = mutableListOf<SocketActionSend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        // Init firebase analytics
        firebaseAnalytics = Firebase.analytics
        // set height statusbar
        val layoutparam = binding.statusBarView.layoutParams
        layoutparam.height = DeviceUtil.getStatusBarHeight(this)
        binding.statusBarView.layoutParams = layoutparam
        window?.statusBarColor = Color.RED
        transparentStatusBar(this)

        LoadingDialog.getInstance(this)?.destroyLoadingDialog()
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)

        viewModel.isSuccess.observeForever(isSuccessObserver)
        shareViewModel.isHaveToken.observeForever(isHaveTokenObserver)
        viewModel.connectSocket()

        if (!rxPreferences.getToken().isNullOrEmpty()) {
            tryHandleNotificationFromBackground(intent, true)
        }

        viewModel.newMessage.observe(this) {
            it?.let {
                when (getCurrentFragment()) {
                    is TopFragment -> {
                        when ((getCurrentFragment() as TopFragment).getInstanceCurrentFragment()) {
                            "HomeFragment" -> {
                                listNotiNewMessage.add(it)
                                checkList()
                            }
                            "RankFragment" -> {
                                checkList()
                                listNotiNewMessage.add(it)
                            }
                            "MyPageFragment" -> {
                                listNotiNewMessage.add(it)
                                checkList()
                            }
                            else -> listNotiNewMessage.clear()
                        }
                    }
                    is UserProfileFragment -> {
                        listNotiNewMessage.add(it)
                        if ((getCurrentFragment() as UserProfileFragment).showBottomDialog())
                            checkList()
                        else checkList()
                    }
                    else -> listNotiNewMessage.clear()
                }
            }
        }
    }

    private fun transparentStatusBar(activity: Activity) {
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        (Objects.requireNonNull(activity) as AppCompatActivity).supportActionBar?.hide()
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = Color.TRANSPARENT
    }

    private fun handleLaunchAppWithDeepLinkMain(intent: Intent, isOpenDirect: Boolean) {
        val data: Uri? = intent.data
        if (data != null && data.isHierarchical) {
            handleDeepLink(data, isOpenDirect)
        }
    }

    fun handleDeepLink(uri: Uri, isOpenDirect: Boolean = false) {
        var linkType = uri.host ?: ""
        val code = uri.getQueryParameter(Define.Intent.CODE) ?: ""
        if (linkType == Define.Intent.ONE_LINK) {
            val linkUri = uri.getQueryParameter(Define.Intent.DEEP_LINK_VALUE)
            val linkUrlScheme = uri.getQueryParameter(Define.Intent.DEEP_LINK_URL_SCHEME)
            if (linkUri != null) {
                linkType = linkUri
            } else if (linkUrlScheme != null) {
                linkType = linkUrlScheme.removePrefix(PREFIX_CARE_APP)
            }
        }
        when (linkType) {
            Define.Intent.NOTICE -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is NewsFragment) {
                    appNavigation.popopBackStackToDetination(R.id.newsFragment)
                } else {
                    val bundle = Bundle()
                    if (isOpenDirect) {
                        bundle.putBoolean(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION, isOpenDirect)
                    }
                    appNavigation.openTopToNewsScreen(bundle)
                }
            }
            Define.Intent.RANKING -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is TopFragment) {
                    shareViewModel.setTabSelected(ShareViewModel.TAB_RANKING_SELECTED)
                } else {
                    appNavigation.openOtherScreenToTopScreen()
                    shareViewModel.setTabSelected(ShareViewModel.TAB_RANKING_SELECTED)
                }
            }
            Define.Intent.BUY_POINT -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is BuyPointFragment) {
                    appNavigation.popopBackStackToDetination(R.id.buyPointFragment)
                } else {
                    val bundle = Bundle()
                    if (isOpenDirect) {
                        bundle.putBoolean(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION, isOpenDirect)
                    }
                    appNavigation.openTopToBuyPointScreen(bundle)
                }
            }
            Define.Intent.FREE_POINT -> {
                appNavigation.containScreenInBackStack(
                    R.id.webViewFragment,
                    handleResult = (
                            { isContainInBackstack, bundle ->
                                if (isContainInBackstack) {
                                    if (bundle != null && bundle.getString(
                                            Define.TITLE_WEB_VIEW,
                                            ""
                                        )
                                        == getString(R.string.point_free)
                                    ) {
                                        appNavigation.popopBackStackToDetination(R.id.webViewFragment)
                                    } else {
                                        val bundle = Bundle().apply {
                                            putString(
                                                Define.TITLE_WEB_VIEW,
                                                getString(R.string.point_free)
                                            )
                                            putString(Define.URL_WEB_VIEW, Define.URL_FREE_POINT)
                                        }
                                        appNavigation.openScreenToWebview(bundle)
                                    }
                                } else {
                                    val bundle = Bundle().apply {
                                        putString(
                                            Define.TITLE_WEB_VIEW,
                                            getString(R.string.point_free)
                                        )
                                        putString(Define.URL_WEB_VIEW, Define.URL_FREE_POINT)
                                    }
                                    appNavigation.openScreenToWebview(bundle)
                                }
                            }
                            )
                )
            }
            Define.Intent.COUNSELOR -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is UserProfileFragment && shareViewModel.getPerformerCode() == code) {
                    appNavigation.popopBackStackToDetination(R.id.userProfileFragment)
                } else {
                    val bundle = Bundle()
                    bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
                    val listData =
                        listOf(ConsultantResponse(code = code))
                    bundle.putSerializable(BUNDLE_KEY.LIST_USER_PROFILE, ArrayList(listData))
                    if (isOpenDirect) {
                        bundle.putBoolean(
                            Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION,
                            isOpenDirect
                        )
                    }
                    currentFragment.dismissAllDialog()
                    appNavigation.openTopToUserProfileScreen(bundle)
                }
            }
            Define.Intent.LABO -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is TopFragment) {
                    shareViewModel.setTabSelected(ShareViewModel.TAB_FAVORITE_SELECTED)
                } else {
                    currentFragment.dismissAllDialog()
                    appNavigation.openOtherScreenToTopScreen()
                    shareViewModel.setTabSelected(ShareViewModel.TAB_FAVORITE_SELECTED)
                }
                shareViewModel.laboTabSelected.value = 0
            }
            Define.Intent.MY_MENU -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is TopFragment) {
                    shareViewModel.setTabSelected(ShareViewModel.TAB_MY_PAGE_SELECTED)
                } else {
                    currentFragment.dismissAllDialog()
                    appNavigation.openOtherScreenToTopScreen()
                    shareViewModel.setTabSelected(ShareViewModel.TAB_MY_PAGE_SELECTED)
                }
            }
            Define.Intent.MESSAGE -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is ChatMessageFragment && shareViewModel.getMessagePerformerCode() == code) {
                    appNavigation.popopBackStackToDetination(R.id.chatMessageFragment)
                } else {
                    val bundle = Bundle()
                    bundle.putString(BUNDLE_KEY.PERFORMER_CODE, code)
                    currentFragment.dismissAllDialog()
                    appNavigation.openTopToChatMessage(bundle)
                }
            }
            Define.Intent.PAYMENT_METHOD, Define.Intent.CREDIT, Define.Intent.BANK -> {
                if (appNavigation.currentFragmentId() != R.id.buyPointFragment) {
                    appNavigation.openWebBuyPointToBuyPointGoogle()
                }
            }
            Define.Intent.BLOG -> {
                val blogId = uri.getQueryParameter(Define.BLOG_ID)
                val bundle = Bundle().apply {
                    putString(Define.BLOG_ID, blogId)
                }
                appNavigation.openScreenToWebview(bundle)
            }
        }
    }

    private fun getUrlWebView(linkType: String): String {
        return when (linkType) {
            Define.Intent.PAYMENT_METHOD -> Define.URL_BUY_POINT
            Define.Intent.CREDIT -> Define.URL_LIST_POINT_CREDIT
            else -> ""
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            networkEvent.observableNetworkState
                .flowOn(Dispatchers.Main)
                .collectLatest { status ->
                    when (status) {
                        is NetworkState.SERVER_NOT_AVAILABLE -> {
                            val bundle = Bundle().apply {
                                putString("type", Define.TYPE_MAINTENANCE)
                            }
                            appNavigation.openMainToMaintenance(bundle)
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.ERROR -> {
                            CommonAlertDialog.getInstanceCommonAlertdialog(
                                this@MainActivity,
                                TYPE_DIALOG.GENERIC
                            )
                                .showDialog()
                                .setDialogTitle(R.string.error_maintenance)
                                .setTextOkButton(R.string.confirm_block_alert)
                                .setOnOkButtonPressed {
                                    it.dismiss()
                                    networkEvent.publish(NetworkState.INITIALIZE)
                                }
                        }
                        is NetworkState.UNAUTHORIZED -> {
                            if (!TextUtils.isEmpty(rxPreferences.getToken())) {
                                rxPreferences.logout()
                                val currentFragment: Fragment =
                                    navHostFragment.childFragmentManager.fragments[0]
                                if (currentFragment is SplashFragment)
                                    appNavigation.openActionToLogin()
                                else {
                                    CommonAlertDialog.getInstanceCommonAlertdialog(
                                        this@MainActivity,
                                        TYPE_DIALOG.UN_AUTHEN
                                    )
                                        .showDialog()
                                        .setDialogTitle(R.string.wrong_email_address_or_password)
                                        .setTextOkButton(R.string.text_OK)
                                        .setOnOkButtonPressed {
                                            it.dismiss()
                                        }
                                    appNavigation.openActionToLogin()
                                }
                            }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.NO_INTERNET -> {
                            CommonAlertDialog.getInstanceCommonAlertdialog(
                                this@MainActivity,
                                TYPE_DIALOG.NO_INTERNET
                            )
                                .showDialog()
                                .setDialogTitle(jp.careapp.core.R.string.no_internet)
                                .setTextOkButton(R.string.ok)
                                .setOnOkButtonPressed {
                                    it.dismiss()
                                }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.BAD_REQUEST -> {
                            CommonAlertDialog.getInstanceCommonAlertdialog(
                                this@MainActivity,
                                TYPE_DIALOG.BAD_REQUEST
                            )
                                .showDialog()
                                .setDialogTitle(R.string.status_400)
                                .setTextOkButton(R.string.confirm_block_alert)
                                .setOnOkButtonPressed {
                                    it.dismiss()
                                    appNavigation.navigateUp()
                                }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.CONNECTION_LOST -> {
                            CommonAlertDialog.getInstanceCommonAlertdialog(
                                this@MainActivity,
                                TYPE_DIALOG.CONNECTION_LOST
                            )
                                .showDialog()
                                .setDialogTitle(R.string.time_out)
                                .setTextOkButton(R.string.confirm_block_alert)
                                .setOnOkButtonPressed {
                                    it.dismiss()
                                }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.FORBIDDEN, NetworkState.NOT_FOUND -> {
                            CommonAlertDialog.getInstanceCommonAlertdialog(
                                this@MainActivity,
                                TYPE_DIALOG.NOT_FOUND
                            )
                                .showDialog()
                                .setDialogTitle(R.string.general_error)
                                .setTextOkButton(R.string.confirm_block_alert)
                                .setOnOkButtonPressed {
                                    it.dismiss()
                                }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                        is NetworkState.GENERIC -> {
                            val currentFragment: Fragment =
                                navHostFragment.childFragmentManager.fragments[0]
                            if (currentFragment is VerifyCodeFragment) {
                                if (status.exception.errors.joinToString() != getString(R.string.authentication_code_incorrect) && status.exception.errors.joinToString() != getString(
                                        R.string.authorization_code_expired
                                    )
                                ) {
                                    CommonAlertDialog.getInstanceCommonAlertdialog(
                                        this@MainActivity,
                                        TYPE_DIALOG.GENERIC
                                    )
                                        .showDialog()
                                        .setDialogTitleWithString(status.exception.errors.joinToString())
                                        .setTextOkButton(R.string.confirm_block_alert)
                                        .setOnOkButtonPressed {
                                            it.dismiss()
                                        }
                                }
                            } else if (currentFragment is SplashFragment) {
                                // nothing
                            } else {
                                var dataError: String
                                try {
                                    dataError = status.exception.errors.joinToString()
                                } catch (e: java.lang.Exception) {
                                    dataError = ""
                                    networkEvent.publish(NetworkState.ERROR)
                                }
                                CommonAlertDialog.getInstanceCommonAlertdialog(this@MainActivity)
                                    .showDialog()
                                    .setDialogTitleWithString(dataError)
                                    .setTextOkButton(R.string.confirm_block_alert)
                                    .setOnOkButtonPressed {
                                        it.dismiss()
                                    }
                            }
                            networkEvent.publish(NetworkState.INITIALIZE)
                        }
                    }
                }
        }
    }

    override fun onDestroy() {
        CommonAlertDialog.getInstanceCommonAlertdialog(this@MainActivity)
            .dismiss()
        super.onDestroy()
    }

    private var isSuccessObserver: Observer<Boolean> = Observer {
        if (it) {
            viewModel.saveDeviceToken()
        }
    }

    private var isHaveTokenObserver: Observer<Boolean> = Observer {
        if (it) {
            if (!rxPreferences.getToken().isNullOrEmpty()) {
                getFcmToken()
                handleDataFromNotification(intent, true)
                handleLaunchAppWithDeepLinkMain(intent, true)
            }
        }
    }

    private fun getFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val fcmToken = instanceIdResult.token
                viewModel.registerDeviceToken(fcmToken)
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        hiddenLoading()
        intent?.let {
            if (!rxPreferences.getToken().isNullOrEmpty()) {
                handleDataFromNotification(it, false)
                handleLaunchAppWithDeepLinkMain(it, false)
            }
        }
    }

    private fun handleDataFromNotification(intent: Intent?, isOpenDirect: Boolean) {
        val bundle = intent!!.getBundleExtra(Define.Intent.DATA_FROM_NOTIFICATION)
        if (bundle != null) {
            var messageType = ""
            if (bundle.containsKey(Define.Intent.TYPE)) {
                messageType = bundle.getString(Define.Intent.TYPE).toString()
            }

            var body = ""
            if (bundle.containsKey(Define.Intent.BODY)) {
                body = bundle.getString(Define.Intent.BODY).toString()
            }

            var url = ""
            if (bundle.containsKey(Define.Intent.URL)) {
                url = bundle.getString(Define.Intent.URL).toString()
                if (url.isNotEmpty()) {
                    messageType = try {
                        Uri.parse(url).host ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                }
            }

            var performerCode = -1
            if (bundle.containsKey(Define.Intent.PERFORMER_CODE)) {
                performerCode = try {
                    bundle.getInt(Define.Intent.PERFORMER_CODE)
                } catch (ex: Exception) {
                    -1
                }
            }

            var questionId = -1
            if (bundle.containsKey(Define.Intent.QUESTION_ID)) {
                questionId = try {
                    bundle.getInt(Define.Intent.QUESTION_ID)
                } catch (ex: Exception) {
                    -1
                }
            }

            showDetailNotification(messageType, body, url, performerCode, questionId, isOpenDirect)
        } else {
            tryHandleNotificationFromBackground(intent, isOpenDirect)
        }
    }

    private fun showDetailNotification(
        typeMessage: String?,
        body: String?,
        url: String?,
        performerCode: Int,
        questionId: Int,
        isOpenDirect: Boolean
    ) {
        when (typeMessage) {
            Define.Intent.MESSAGE -> {
                if (CommonAlertDialog.getInstanceCommonAlertdialog(this).isShowing) {
                    CommonAlertDialog.getInstanceCommonAlertdialog(this).dismiss()
                }
                appNavigation.containScreenInBackStack(
                    R.id.chatMessageFragment,
                    handleResult = (
                            { isContainInBackstack, bundle ->
                                if (isContainInBackstack) {
                                    if (bundle != null && bundle.getString(
                                            BUNDLE_KEY.PERFORMER_CODE,
                                            ""
                                        )
                                            .equals(performerCode.toString())
                                    ) {
                                        appNavigation.popopBackStackToDetination(R.id.chatMessageFragment)
                                        if (getCurrentFragment() is ChatMessageFragment) {
                                            (getCurrentFragment() as ChatMessageFragment).apply {
                                                reloadData()
                                            }
                                        }
                                    } else {
                                        var bundle = Bundle()
                                        bundle.putString(
                                            BUNDLE_KEY.PERFORMER_CODE,
                                            performerCode.toString()
                                        )
                                        appNavigation.openMainToChatMessage(bundle)
                                    }
                                } else {
                                    appNavigation.containScreenInBackStack(
                                        R.id.topFragment,
                                        handleResult = (
                                                { isContainInBackstack, bundle ->
                                                    if (isContainInBackstack) {
                                                        val bundle = Bundle()
                                                        bundle.putString(
                                                            BUNDLE_KEY.PERFORMER_CODE,
                                                            performerCode.toString()
                                                        )
                                                        appNavigation.openMainToChatMessage(bundle)
                                                    } else {
                                                        appNavigation.openOtherScreenToTopScreen()
                                                        shareViewModel.valuePassFromMain.value =
                                                            performerCode
                                                    }
                                                }
                                                )
                                    )
                                }
                            }
                            )
                )
            }
            Define.Intent.ONLINE -> {
                val currentFragment: Fragment =
                    navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is UserProfileFragment) {
                    if (shareViewModel.getPerformerCode() == performerCode.toString()) {
                        appNavigation.popopBackStackToDetination(R.id.userProfileFragment)
                    } else {
                        var bundle = Bundle()
                        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
                        val listData =
                            listOf(ConsultantResponse(code = performerCode.toString()))
                        bundle.putSerializable(BUNDLE_KEY.LIST_USER_PROFILE, ArrayList(listData))
                        if (isOpenDirect) {
                            bundle.putBoolean(
                                Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION,
                                isOpenDirect
                            )
                        }
                        appNavigation.openTopToUserProfileScreen(bundle)
                    }
                } else {
                    var bundle = Bundle()
                    bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
                    val listData =
                        listOf(ConsultantResponse(code = performerCode.toString()))
                    bundle.putSerializable(BUNDLE_KEY.LIST_USER_PROFILE, ArrayList(listData))
                    if (isOpenDirect) {
                        bundle.putBoolean(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION, isOpenDirect)
                    }
                    appNavigation.openTopToUserProfileScreen(bundle)
                }
            }
            else -> {
                try {
                    handleDeepLink(Uri.parse(url), isOpenDirect)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun getCurrentFragment(): Fragment {
        return navHostFragment.childFragmentManager.fragments[0]
    }

    private fun tryHandleNotificationFromBackground(intent: Intent?, isOpenDirect: Boolean) {
        intent?.extras?.let { backgroundBundle ->
            var body: String? = ""
            if (backgroundBundle.containsKey(Define.Intent.BODY)) {
                body = backgroundBundle.getString(Define.Intent.BODY)?.trim()
            }

            var messageType: String? = null
            if (backgroundBundle.containsKey(Define.Intent.TYPE)) {
                messageType = backgroundBundle.getString(Define.Intent.TYPE)
            }

            var performerCode = -1
            if (backgroundBundle.containsKey(Define.Intent.PERFORMER_CODE)) {
                val tmp = backgroundBundle.getString(Define.Intent.PERFORMER_CODE)
                performerCode = try {
                    tmp!!.toInt()
                } catch (ex: Exception) {
                    -1
                }
            }

            var url: String? = ""
            if (backgroundBundle.containsKey(Define.Intent.URL)) {
                url = backgroundBundle.getString(Define.Intent.URL)
                url?.let { it -> messageType = it.split(getString(R.string.regex_link))[1] }
            }

            val numberUnread: Int
            if (messageType.equals(Define.Intent.MESSAGE)) {
                val preferences: RxPreferences = AppPreferences(this)
                numberUnread = preferences.getNumberUnreadMessage() + 1
                preferences.saveNumberUnreadMessage(numberUnread)
                val intent1 = Intent()
                intent1.action = Define.Intent.ACTION_RECEIVE_NOTIFICATION
                val bundle1 = Bundle()
                bundle1.putInt(Define.Intent.NUMBER_UNREAD_MESSAGE, numberUnread)
                intent1.putExtra(Define.Intent.DATA_FROM_NOTIFICATION, bundle1)
                this.sendBroadcast(intent1)
            }

            var questionId = -1
            if (backgroundBundle.containsKey(Define.Intent.QUESTION_ID)) {
                val tmp = backgroundBundle.getString(Define.Intent.QUESTION_ID)
                questionId = try {
                    tmp!!.toInt()
                } catch (ex: Exception) {
                    -1
                }
            }

            if (backgroundBundle.containsKey(Define.Intent.TYPE)) {
                messageType = messageType.toString()
            }

            if (backgroundBundle.containsKey(Define.Intent.BODY)) {
                body = body.toString()
            }

            if (backgroundBundle.containsKey(Define.Intent.URL)) {
                url = url ?: ""
                if (!url.isNullOrEmpty()) {
                    messageType = try {
                        Uri.parse(url).host ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                }
            }

            showDetailNotification(messageType, body, url, performerCode, questionId, isOpenDirect)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onBackPressed() {
        val currentFragment: Fragment =
            navHostFragment.childFragmentManager.fragments[0]

        if (currentFragment is RegistrationFragment) {
            super.onBackPressed()
        } else if (currentFragment is TopFragment) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
            if (currentFragment is OnBackPressedListener) (currentFragment as OnBackPressedListener).onBackPressed()
        }
    }


    private fun checkList() {
        currentList.addAll(listNotiNewMessage)
        listNotiNewMessage.clear()

        if (currentList.size == 1) {
            showNotification(currentList[0])
        }
    }

    private fun showNotification(item: SocketActionSend) {
        val notifyView = findViewToShowNotification() ?: return
        val position = findPositionToShow() ?: return

        notifyView.notificationView.visibility = View.VISIBLE
        notifyView.tvContent.text = getString(R.string.content_notification, item.senderName)
        if (item.senderCode == Define.Intent.SUPPORTER_CODE) {
            notifyView.itemCode = Define.Intent.CODE_OPEN_CHAT_SUPPORTER
        } else {
            notifyView.itemCode = item.senderCode
        }
        when (position) {
            Position.DOWN -> {
                currentList.remove(item)
                animShowToDown(notifyView)
            }

            Position.TOP -> {
                currentList.remove(item)
                animShowToUp(notifyView)
            }
        }
    }

    private fun findViewToShowNotification(): NotificationView? {
        listNotificationView.forEach {
            if (it.state == StateView.HIDE) {
                return it
            }
        }
        return null
    }

    private fun findPositionToShow(): Position? {
        if (findNotificationOnBottom() == null) {
            return Position.DOWN
        }

        if (findNotificationOnTop() == null) {
            return Position.TOP
        }
        return null
    }

    private fun findNotificationOnTop(): NotificationView? {
        listNotificationView.forEach {
            if (it.state == StateView.SHOWING_TO_UP || it.state == StateView.SHOW_UP) {
                return it
            }
        }
        return null
    }

    private fun findNotificationOnBottom(): NotificationView? {
        listNotificationView.forEach {
            if (it.state == StateView.SHOWING_TO_DOWN || it.state == StateView.SHOW_DOWN || it.state == StateView.MOVE_UP_TO_DOWN) {
                return it
            }
        }
        return null
    }

    private fun animShowToUp(
        notificationView: NotificationView
    ) {
        val height =
            0 - heightToolbar - 2 * getSpacingAnim() - notificationView.notificationView.height
        val animationUp =
            animMoveNotification(notificationView.notificationView, 0f, height)
        animationUp.apply {
            doOnStart {
                notificationView.notificationView.animate().alpha(1.0f).duration =
                    Define.DURATION_ALPHA
                notificationView.state = StateView.SHOWING_TO_UP
            }
            doOnEnd {
                notificationView.state = StateView.SHOW_UP
                handleTimeHideView(notificationView)
            }
            interpolator = DecelerateInterpolator()
            animationUp.start()
        }
    }

    private fun animShowToDown(notificationView: NotificationView) {
        val height = 0 - getSpacingAnim() - heightToolbar
        val animToDown = animMoveNotification(notificationView.notificationView, 0f, height)
        animToDown.apply {
            doOnStart {
                notificationView.state = StateView.SHOWING_TO_DOWN
                notificationView.notificationView.animate().alpha(1.0f).duration =
                    Define.DURATION_ALPHA
            }
            doOnEnd {
                notificationView.state = StateView.SHOW_DOWN
                handleTimeHideView(notificationView)
            }
            interpolator = DecelerateInterpolator()
            animToDown.start()
        }
    }

    private fun animMoveUpToDown(notificationView: NotificationView) {
        val animUpToDown = animMoveNotification(
            notificationView.notificationView,
            0 - heightToolbar - 2 * getSpacingAnim() - notificationView.notificationView.height,
            0 - heightToolbar - getSpacingAnim()
        )
        animUpToDown.apply {
            doOnStart {
                notificationView.state = StateView.MOVE_UP_TO_DOWN
            }
            doOnEnd {
                notificationView.state = StateView.SHOW_DOWN
            }
            animUpToDown.start()
        }
    }

    private fun animHiding(notificationView: NotificationView) {
        val animHiding = animMoveNotification(
            notificationView.notificationView,
            0 - heightToolbar - getSpacingAnim(),
            0f
        )
        animHiding.apply {
            doOnStart {
                notificationView.state = StateView.HIDING
                notificationView.notificationView.animate().alpha(0.0f).duration =
                    Define.DURATION_ALPHA
            }

            doOnEnd {
                notificationView.state = StateView.HIDE
                if (currentList.isNotEmpty()) {
                    showNotification(currentList[0])
                }
            }
            notificationView.notificationView.visibility = View.INVISIBLE
            animHiding.start()
        }
    }

    private fun handleTimeHideView(notificationView: NotificationView) {
        notificationView.timerView?.cancel()
        notificationView.timerView =
            object : CountDownTimer(Define.TIME_SHOW_NOTIFICATION, Define.COUNT_DOWN_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    val view = findNotificationOnTop()
                    if (view != null) {
                        animMoveUpToDown(view)
                    }
                    animHiding(notificationView)
                }
            }.start()
    }

    fun openChatByNotification(view: View) {
        val bundle = Bundle()

        listNotificationView.forEach {
            if (view.id == it.notificationView.id) {
                bundle.putString(
                    BUNDLE_KEY.PERFORMER_CODE,
                    it.itemCode
                )
                appNavigation.openTopToChatMessage(bundle)
                it.timerView?.cancel()
                it.timerView?.onFinish()
                return@forEach
            }
        }
    }

    fun closeNotification(view: View) {
        listNotificationView.forEach {
            if (view.id == it.ivCloseView.id) {
                it.timerView?.cancel()
                it.timerView?.onFinish()
            }
        }
    }

    private fun getSpacingAnim() = resources.getDimensionPixelSize(R.dimen.margin_10)

    var heightToolbar = 0f

    private fun animMoveNotification(
        view: View,
        fromY: Float,
        toY: Float,
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            view,
            "translationY",
            fromY,
            toY
        ).setDuration(600)
    }
}