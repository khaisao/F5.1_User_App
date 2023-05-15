package jp.careapp.counseling.android.ui.profile.detail_user

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.GalleryAdapter
import jp.careapp.counseling.android.data.model.live_stream.ConnectResult
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.GalleryResponse
import jp.careapp.counseling.android.data.network.ThumbnailImageResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.live_stream.CallConnectionDialog
import jp.careapp.counseling.android.ui.profile.block_report.BlockAndReportBottomFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.FLAX_LOGIN_AUTH_RESPONSE
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.ROOT_SCREEN
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.SCREEN_DETAIL
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.USER_PROFILE
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.VIEW_STATUS
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SocketInfo
import jp.careapp.counseling.android.utils.SocketInfo.RESULT_NG
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.extensions.hasPermissions
import jp.careapp.counseling.android.utils.performer_extension.PerformerRankingHandler
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatus
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.FragmentDetailUserProfileBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserProfileFragment :
    BaseFragment<FragmentDetailUserProfileBinding, DetailUserProfileViewModel>(),
    CallConnectionDialog.CallingCancelListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    override val layoutId = R.layout.fragment_detail_user_profile

    private val viewModel: DetailUserProfileViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    private var typeScreen = ""

    override fun getVM(): DetailUserProfileViewModel = viewModel

    // data screen
    // user load from server
    private var consultantResponse: ConsultantResponse? = null

    // user in list user from local
    private var consultantResponseLocal: ConsultantResponse? = null

    private var isFirstChat: Boolean? = null
    private var previousScreen = ""
    private var numberTimeCanScrollDown = 0
    private var numberMaxTimeCanScrollDown = 0
    private var viewerType = 0

    // item check favorite when first chat
    private var isShowFromUserDisable: Boolean = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkPoint()
        }
    }

    private lateinit var  galleryAdapter:  GalleryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = arguments
        if (bundle != null) {
            consultantResponseLocal =
                bundle.getSerializable(USER_PROFILE) as? ConsultantResponse
            previousScreen = bundle.getString(BUNDLE_KEY.SCREEN_TYPE, "")
        }
        loadData()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initView() {
        super.initView()
        binding.avatarIv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.avatarIv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                galleryAdapter = GalleryAdapter(requireContext(), binding.avatarIv.height) {
                    binding.avatarIv.loadImage(it.image?.url, R.drawable.default_avt_performer)
                }
                binding.rvGallery.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.rvGallery.adapter = galleryAdapter
            }
        })

        binding.scrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                lifecycleScope.launch {
                    delay(1000)
                    binding.swipeRefreshLayout.isEnabled = true
                }
            } else {
                binding.swipeRefreshLayout.isEnabled = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.avatarIv.loadImage(consultantResponse?.imageUrl, R.drawable.default_avt_performer)
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.ivBack.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.navigateUp()
            }
        }

        binding.addFavoriteTv.setOnClickListener {
            if (!isDoubleClick) {
                consultantResponse?.let {
                    isShowFromUserDisable = false
                    viewModel.addUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
        }
        binding.removeFavoriteTv.setOnClickListener {
            if (!isDoubleClick) {
                consultantResponse?.let {
                    viewModel.removeUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
        }
        // user online
        binding.ivMessage.setOnClickListener {
            if (!isDoubleClick) {
                openChatScreen()
            }
        }

        binding.ivArrowDown.setOnClickListener {
            binding.ivArrowUp.visibility = VISIBLE
            numberTimeCanScrollDown -= 1
            if (numberTimeCanScrollDown == 0) {
                binding.ivArrowDown.visibility = GONE
            } else {
                binding.ivArrowDown.visibility = VISIBLE
            }
            val layoutManager = binding.rvGallery.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findLastVisibleItemPosition()
            val lastVisibleItemIndex: Int = currentPosition
            binding.rvGallery.smoothScrollToPosition(lastVisibleItemIndex + 3)
        }

        binding.ivArrowUp.setOnClickListener {
            binding.ivArrowDown.visibility = VISIBLE
            numberTimeCanScrollDown += 1
            val layoutManager = binding.rvGallery.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findLastVisibleItemPosition()
            if (numberMaxTimeCanScrollDown == numberTimeCanScrollDown) {
                binding.ivArrowUp.visibility = GONE
            } else {
                binding.ivArrowUp.visibility = VISIBLE
            }
            binding.rvGallery.smoothScrollToPosition(currentPosition - 6)
        }

        binding.llCallConsult.setOnClickListener {
            if (!isDoubleClick) {
                when {
                    hasPermissions(arrayOf(RECORD_AUDIO)) -> {
                        checkPoint()
                    }
                    shouldShowRequestPermissionRationale(RECORD_AUDIO) -> {
                        showDialogNeedMicrophonePermission()
                    }
                    else -> {
                        showDialogRequestMicrophonePermission()
                    }
                }
                viewerType = 0
                viewModel.viewerStatus = 0
            }
        }

        binding.llPeep.setOnClickListener {
            if (!isDoubleClick) {
                viewerType = 1
                viewModel.viewerStatus = 1
                checkPointForPeep()
            }
        }

        binding.ivPrivateDelivery.setOnClickListener {
            openChatScreen()
        }
    }

    override fun callingCancel(isError: Boolean) {
        viewModel.cancelCall(isError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.userProfileResult.removeObservers(viewLifecycleOwner)
        viewModel.statusFavorite.removeObservers(viewLifecycleOwner)
        viewModel.statusRemoveFavorite.removeObservers(viewLifecycleOwner)
        viewModel.isFirstChat.removeObservers(viewLifecycleOwner)
        viewModel.blockUserResult.removeObservers(viewLifecycleOwner)
        viewModel.userGallery.removeObservers(viewLifecycleOwner)
        viewModel.connectResult.removeObservers(viewLifecycleOwner)
        viewModel.isButtonEnable.removeObservers(viewLifecycleOwner)
        viewModel.isLoginSuccess.removeObservers(viewLifecycleOwner)
    }

    private fun openChatScreen(isShowFreeMess: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(
            BUNDLE_KEY.PERFORMER_CODE,
            consultantResponseLocal?.code ?: ""
        )
        bundle.putString(
            BUNDLE_KEY.PERFORMER_NAME,
            consultantResponse?.name ?: ""
        )
        bundle.putBoolean(BUNDLE_KEY.PROFILE_SCREEN, false)
        bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
        bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponse?.callRestriction ?: 0)
        appNavigation.openDetailUserToChatMessage(bundle)
    }

    private fun checkPoint() {
        if (rxPreferences.getPoint() < 1000) {
            showDialogRequestBuyPoint()
        } else {
            showDialogConfirmCall()
        }
    }

    private fun checkPointForPeep() {
        if (rxPreferences.getPoint() == 0) {
            showDialogRequestBuyPointForPeep()
        } else {
            showDialogConfirmCall()
        }
    }

    private fun showDialogRequestMicrophonePermission() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_title_request_mic)
            .setContent(R.string.msg_explain_request_mic)
            .setTextPositiveButton(R.string.accept_permission)
            .setTextNegativeButton(R.string.denied_permission)
            .setOnPositivePressed {
                requestPermissionLauncher.launch(RECORD_AUDIO)
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogNeedMicrophonePermission() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_need_mic_permission)
            .setTextPositiveButton(R.string.setting)
            .setTextNegativeButton(R.string.cancel)
            .setOnPositivePressed {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context?.packageName, null)
                })
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogRequestBuyPoint() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.live_stream_title_request_buy_point)
            .setTextPositiveButton(R.string.live_stream_buy_point)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed { dialog ->
                val bundle = Bundle().also {
                    it.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
                }
                handleBuyPoint.buyPoint(childFragmentManager, bundle,
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {
                            checkPoint()
                        }
                    }
                )
                dialog.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogRequestBuyPointForPeep() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.live_stream_peep_title_request_buy_point)
            .setTextPositiveButton(R.string.live_stream_buy_point)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed { dialog ->
                val bundle = Bundle().also {
                    it.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
                }
                handleBuyPoint.buyPoint(childFragmentManager, bundle,
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {
                            checkPointForPeep()
                        }
                    }
                )
                dialog.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogConfirmCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(getString(R.string.content_confirm_call, consultantResponse?.name))
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed {
                it.dismiss()
                openCalling()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun openCalling() {
        consultantResponse?.let { performer ->
            val status =
                PerformerStatusHandler.getStatus(performer.callStatus, performer.chatStatus)
            performer.code?.let { viewModel.connectLiveStream(it, status) }
        }
    }

    fun doBuyPoint() {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
        handleBuyPoint.buyPoint(
            childFragmentManager,
            bundle,
            object : BuyPointBottomFragment.HandleBuyPoint {
                override fun buyPointSucess() {
                    activity?.let {
                        handleOpenChatScreen()
                    }
                }
            }
        )
    }

    private fun handleOpenChatScreen(isShowFreeMess: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(
            BUNDLE_KEY.PERFORMER_CODE,
            consultantResponse?.code ?: ""
        )
        bundle.putString(
            BUNDLE_KEY.PERFORMER_NAME,
            consultantResponse?.name ?: ""
        )
        bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
        bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponse?.callRestriction ?: 0)
        if (BUNDLE_KEY.CHAT_MESSAGE == previousScreen) {
            appNavigation.navigateUp()
        } else {
            appNavigation.openDetailUserToChatMessage(bundle)
        }
    }

    private fun loadData() {
        consultantResponseLocal?.code?.let {
            viewModel.loadDetailUser(it)
        }
    }

    private fun setButtonEnable(isEnable: Boolean) {
        binding.ivMessage.isEnabled = isEnable
        binding.llCallConsult.isEnabled = isEnable
        binding.llPeep.isEnabled = isEnable
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.userProfileResult.observe(viewLifecycleOwner, handleResultDetailUser)
        viewModel.statusFavorite.observe(viewLifecycleOwner, handleResultStatusFavorite)
        viewModel.statusRemoveFavorite.observe(viewLifecycleOwner, handleResultStatusUnFavorite)
        viewModel.isFirstChat.observe(viewLifecycleOwner, handleFirstChat)
        viewModel.blockUserResult.observe(
            viewLifecycleOwner, handleBlockResult
        )
        viewModel.userGallery.observe(viewLifecycleOwner, handleResultUserGallery)
        viewModel.connectResult.observe(viewLifecycleOwner, connectResultHandle)
        viewModel.isButtonEnable.observe(viewLifecycleOwner, buttonEnableHandle)
        viewModel.isLoginSuccess.observe(viewLifecycleOwner, loginSuccessHandle)
    }

    private val connectResultHandle: Observer<ConnectResult> = Observer {
        if (it.result != SocketInfo.RESULT_NONE) {
            consultantResponse?.let { performerResponse ->
                run {
                    val fragment: Fragment? =
                        childFragmentManager.findFragmentByTag("CallConnectionDialog")
                    val dialog: CallConnectionDialog
                    if (fragment != null) {
                        dialog = fragment as CallConnectionDialog
                        dialog.setCallingCancelListener(this@DetailUserProfileFragment)
                        if (it.result == RESULT_NG) {
                            dialog.setMessage(it.message, true)
                        } else {
                            dialog.setMessage(getString(R.string.call_content))
                        }
                    } else {
                        val message =
                            if (it.result == RESULT_NG) it.message else getString(R.string.call_content)
                        val isError = it.result == RESULT_NG
                        dialog =
                            CallConnectionDialog.newInstance(performerResponse, message, isError)
                        dialog.setCallingCancelListener(this@DetailUserProfileFragment)
                        dialog.show(childFragmentManager, "CallConnectionDialog")
                    }
                }
            }
        }
    }

    private val loginSuccessHandle: Observer<Boolean> = Observer {
        if (it) {
            val fragment: Fragment? = childFragmentManager.findFragmentByTag("CallConnectionDialog")
            if (fragment != null) (fragment as CallConnectionDialog).dismiss()
            val bundle = Bundle().apply {
                putSerializable(FLAX_LOGIN_AUTH_RESPONSE, viewModel.flaxLoginAuthResponse)
                putSerializable(USER_PROFILE, consultantResponse)
                putInt(VIEW_STATUS, viewerType)
                putInt(ROOT_SCREEN, SCREEN_DETAIL)
            }
            appNavigation.openUserDetailToLiveStream(bundle)
            viewModel.resetData()
        }
    }

    private val buttonEnableHandle: Observer<Boolean> = Observer {
        setButtonEnable(it)
    }

    private var handleResultUserGallery: Observer<List<GalleryResponse>?> = Observer {
        val uriNoImage =
            "android.resource://" + requireContext().packageName + "/" + R.drawable.default_avt_performer
        val itemNoGallery = GalleryResponse(
            thumbnailImage = ThumbnailImageResponse(url = uriNoImage),
            comment = ""
        )
        if (it != null && it.isNotEmpty()) {
            val listGallery = it.toMutableList()
            when {
                listGallery.size <= 3 -> {
                    numberTimeCanScrollDown = 0
                    binding.ivArrowDown.visibility = GONE
                    if (it.size % 3 == 1) {
                        listGallery.add(
                            itemNoGallery
                        )
                        listGallery.add(
                            itemNoGallery
                        )
                    } else if (it.size % 3 == 2) {
                        listGallery.add(
                            itemNoGallery
                        )
                    }
                }
                it.size % 3 == 0 -> {
                    numberTimeCanScrollDown = it.size / 3 - 1
                }
                it.size % 3 != 0 -> {
                    numberTimeCanScrollDown = it.size / 3
                }
            }
            if (numberTimeCanScrollDown >= 1) {
                binding.ivArrowDown.visibility = VISIBLE
            }
            numberMaxTimeCanScrollDown = numberTimeCanScrollDown
            galleryAdapter.submitList(listGallery)
        } else {
            val listNoGallery = listOf(
                itemNoGallery,
                itemNoGallery,
                itemNoGallery
            )
            galleryAdapter.submitList(listNoGallery)
            binding.ivArrowDown.visibility = GONE
        }
    }

    private var handleResultDetailUser: Observer<ConsultantResponse?> = Observer {
        if (it != null) {
            consultantResponse = it
            showDataUserProfile(it)
            setClickForDialogBlock(it)
        } else {
            consultantResponse = consultantResponseLocal
            if (consultantResponseLocal != null) {
                showDataUserProfile(consultantResponseLocal!!)
                setClickForDialogBlock(consultantResponseLocal!!)
            }
        }
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

    private var handleResultStatusFavorite: Observer<Boolean> = Observer {
        if (it) {
            // when user is first chat and disable
            if (isShowFromUserDisable) {
                viewModel.statusFavorite.value = false
            }
            setIconFavoriteWhenClick(true)
        }
    }

    private var handleResultStatusUnFavorite: Observer<Boolean> = Observer {
        if (it) {
            setIconFavoriteWhenClick(false)
        }
    }

    private var handleFirstChat: Observer<Boolean> = Observer {
        this.isFirstChat = it
    }

    private fun setIconFavoriteWhenLoadData(isFavorite: Boolean) {
        if (isFavorite) {
            binding.apply {
                removeFavoriteTv.visibility = VISIBLE
                addFavoriteTv.visibility = GONE
            }

        } else {
            binding.apply {
                removeFavoriteTv.visibility = GONE
                addFavoriteTv.visibility = VISIBLE
            }
        }
    }

    private fun setIconFavoriteWhenClick(isFavorite: Boolean) {
        if (isFavorite) {
            binding.apply {
                removeFavoriteTv.visibility = VISIBLE
                addFavoriteTv.visibility = GONE
                tvMemberCount.text = (tvMemberCount.text.toString().toInt() + 1).toString()
            }

        } else {
            binding.apply {
                removeFavoriteTv.visibility = GONE
                addFavoriteTv.visibility = VISIBLE
                tvMemberCount.text = (tvMemberCount.text.toString().toInt() - 1).toString()
            }
        }
    }

    private fun setClickForDialogBlock(consultantResponse: ConsultantResponse) {
        consultantResponse.also { user ->
            binding.ivMore.setOnClickListener {
                if (!isDoubleClick) {
                    BlockAndReportBottomFragment.showBlockAndReportBottomSheet(
                        parentFragmentManager,
                        object : BlockAndReportBottomFragment.ClickItemView {
                            override fun clickBlock() {
                                if (!isDoubleClick) {
                                    user.let { data ->
                                        CommonAlertDialog.getInstanceCommonAlertdialog(
                                            requireContext()
                                        )
                                            .showDialog()
                                            .setDialogTitleWithString(
                                                resources.getString(R.string.block_this_woman)
                                            )
                                            .setTextPositiveButton(R.string.block_user)
                                            .setTextNegativeButton(R.string.cancel)
                                            .setOnPositivePressed {
                                                it.dismiss()
                                                activity?.let { it1 ->
                                                    viewModel.handleClickBlock(
                                                        data.code ?: "",
                                                        it1
                                                    )
                                                }
                                            }.setOnNegativePressed {
                                                it.dismiss()
                                            }
                                    }

                                }
                            }

                            override fun clickReport() {
                                if (!isDoubleClick) {
                                    val bundle = Bundle()
                                    bundle.putString(USER_PROFILE, user.code)
                                    appNavigation.openUserProfileToReportScreen(bundle)

                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun showDataUserProfile(consultantResponse: ConsultantResponse) {
        consultantResponse.also { user ->
            binding.apply {

                rvGallery.isNestedScrollingEnabled = false

                Glide.with(this@DetailUserProfileFragment).asGif()
                    .load(R.drawable.ic_ballon_call).into(ivBallonLiveGl50)
                Glide.with(this@DetailUserProfileFragment).asGif()
                    .load(R.drawable.ic_ballon_peep).into(ivBallonPeep)

                val status = PerformerStatusHandler.getStatus(user.callStatus,user.chatStatus)

                val statusText = PerformerStatusHandler.getStatusText(status, requireContext().resources)

                val statusBg = PerformerStatusHandler.getStatusBg(status)

                presenceStatusTv.text = statusText

                presenceStatusTv.setBackgroundResource(statusBg)

                ivState.setImageResource(
                    when (user.stage) {
                        BRONZE -> {
                            R.drawable.ic_state_bronze
                        }
                        SILVER -> {
                            R.drawable.ic_state_silver
                        }
                        GOLD -> {
                            R.drawable.ic_state_gold
                        }
                        else -> {
                            R.drawable.ic_state_platinum
                        }
                    }
                )

                binding.ivStateBeginner.visibility = if (user.isRookie == 1) VISIBLE else GONE

                if (user.profilePattern == 1) {
                    avatarIv.visibility = VISIBLE
                    avatarIv.loadImage(user.imageUrl, R.drawable.default_avt_performer, false)
                } else {
                    avatarIv.visibility = GONE
                }

                val bustSize = requireContext().getBustSize(user.bust)

                if (bustSize == "") {
                    tvBust.visibility = GONE
                } else {
                    tvBust.visibility = VISIBLE
                    tvBust.text = bustSize
                }

                tvMemberCount.text = user.favoriteMemberCount.toString()

                tvMessageNotice.text = user.messageOfTheDay

                tvMessageOfTheDay.text = user.message

                tvAge.text = user.age.toString() + resources.getString(R.string.age_raw)

                userNameTv.text = user.name

                if (status == PerformerStatus.LIVE_STREAM) {
                    ivMessage.setImageResource(R.drawable.ic_message_small_detail)
                    tvLiveStreamCount.text =
                        (user.loginMemberCount + user.peepingMemberCount).toString()
                    llStatusViewerCount.visibility = VISIBLE
                } else {
                    ivMessage.setImageResource(R.drawable.ic_message_large_detail)
                    llStatusViewerCount.visibility = GONE
                }

                llCallConsult.visibility =
                    if (status == PerformerStatus.WAITING || status == PerformerStatus.LIVE_STREAM) VISIBLE else GONE
                ivBallonPeep.visibility =
                    if (status == PerformerStatus.LIVE_STREAM) VISIBLE else GONE
                llPeep.visibility = if (status == PerformerStatus.LIVE_STREAM) VISIBLE else GONE
                ivPrivateDelivery.visibility =
                    if (status == PerformerStatus.PRIVATE_LIVE_STREAM || status == PerformerStatus.PREMIUM_PRIVATE_LIVE_STREAM || status == PerformerStatus.OFFLINE) VISIBLE else GONE
                ivBallonLiveGl50.visibility =
                    if (status == PerformerStatus.WAITING) VISIBLE else GONE

                setIconFavoriteWhenLoadData(user.isFavorite)
                binding.imgRanking.setImageResource(
                    PerformerRankingHandler.getImageViewForRank(
                        user.ranking,
                        user.recommendRanking
                    )
                )
            }
        }
    }

    companion object {
        const val BRONZE = 1
        const val SILVER = 2
        const val GOLD = 3
        const val PLATINUM = 4
        const val DIAMOND = 6

        @JvmStatic
        fun getInstance(
            position: Int,
            listPerformer: ArrayList<ConsultantResponse>,
            previousScreen: String
        ): DetailUserProfileFragment {
            val detailUserProfileFragment = DetailUserProfileFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY.SCREEN_TYPE, previousScreen)
            val performer =
                if (listPerformer.size > position) listPerformer.get(position) else null
            bundle.putSerializable(USER_PROFILE, performer)
            detailUserProfileFragment.arguments = bundle
            return detailUserProfileFragment
        }
    }
}
