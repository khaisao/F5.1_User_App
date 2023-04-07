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
import jp.careapp.counseling.android.ui.buy_point.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.calling.CallConnectionDialog
import jp.careapp.counseling.android.ui.calling.CallingViewModel
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point.PurchasePointBottomSheet
import jp.careapp.counseling.android.ui.profile.block_report.BlockAndReportBottomFragment
import jp.careapp.counseling.android.ui.profile.tab_review.TabReviewFragment
import jp.careapp.counseling.android.ui.profile.tab_user_info_detail.TabDetailUserProfileFragment
import jp.careapp.counseling.android.ui.profile.update_trouble_sheet.TroubleSheetUpdateFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.FLAX_LOGIN_AUTH_RESPONSE
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.USER_PROFILE
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.VIEW_STATUS
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SocketInfo.RESULT_NG
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.extensions.hasPermissions
import jp.careapp.counseling.databinding.FragmentDetailUserProfileBinding
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
    private val callingViewModel: CallingViewModel by activityViewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    private var typeScreen = ""

    override fun getVM(): DetailUserProfileViewModel = viewModel

    // data screen
    // user load from server
    private var consultantResponse: ConsultantResponse? = null

    // user in list user from local
    private var consultantResponseLocal: ConsultantResponse? = null

    private var userInforTabAdapter: UserInforTabAdapter? = null
    private var tabDetailUserProfileFragment: TabDetailUserProfileFragment? = null
    private var tabReviewFragment: TabReviewFragment? = null
    private var isFirstChat: Boolean? = null
    private var previousScreen = ""
    private var position: Int = 0
    private var numberTimeCanScrollDown = 0
    private var numberMaxTimeCanScrollDown = 0
    private var loginType = 0

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
                bundle.getSerializable(BUNDLE_KEY.USER_PROFILE) as? ConsultantResponse
            previousScreen = bundle.getString(BUNDLE_KEY.SCREEN_TYPE, "")
        }
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }



    override fun initView() {
        super.initView()
        consultantResponseLocal?.let {
//            initViewpager(it)
        }


        binding.avatarIv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.avatarIv.viewTreeObserver.removeOnGlobalLayoutListener(this);
                galleryAdapter = GalleryAdapter(requireContext(), binding.avatarIv.height)
                binding.rvGallery.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.rvGallery.adapter = galleryAdapter
            }
        })

    }


    override fun setOnClick() {
        super.setOnClick()
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
                loginType = 0
                viewModel.viewerStatus = 0
            }
        }

        binding.llPeep.setOnClickListener {
            if (!isDoubleClick) {
                checkPoint()
                loginType = 1
                viewModel.viewerStatus = 1
            }
        }
    }

    override fun callingCancel() {
        viewModel.cancelCall()
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
        if (isFirstChat == null) {
            consultantResponseLocal?.code?.let { it1 ->
                viewModel.loadMailInfo(
                    it1
                )
            }
        } else {
            isFirstChat?.let {
                if (it) {
                    // transition to trouble sheet screen
                    val bundle = Bundle()
                    bundle.putString(
                        BUNDLE_KEY.PERFORMER_CODE,
                        consultantResponse?.code ?: ""
                    )
                    bundle.putString(
                        BUNDLE_KEY.PERFORMER_NAME,
                        consultantResponse?.name ?: ""
                    )
                    bundle.putBoolean(BUNDLE_KEY.PROFILE_SCREEN, false)
                    bundle.putInt(
                        BUNDLE_KEY.FIRST_CHAT,
                        TroubleSheetUpdateFragment.FIRST_CHAT
                    )
                    bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
                    bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponse?.callRestriction ?: 0)
                    appNavigation.openDetailUserToChatMessage(bundle)
                } else {
                    if ((rxPreferences.getPoint() == 0)) {
                        doBuyPoint()
                    } else {
                        handleOpenChatScreen(isShowFreeMess)
                    }
                }
            }
        }
    }

    private fun checkPoint() {
        if (rxPreferences.getPoint() < 1000) {
            showDialogRequestBuyPoint()
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
            .setContent(R.string.msg_need_mic_permission)
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
                handleBuyPoint.buyPointLiveStream(childFragmentManager, bundle,
                    object : PurchasePointBottomSheet.PurchasePointCallback {
                        override fun purchasePointSuccess() {
                            checkPoint()
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
                openChatScreen(true)
            }
    }

    private fun showDialogWarningDuringCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_warning_during_call)
            .setTextPositiveButton(R.string.text_OK)
    }

    private fun openCalling() {
        consultantResponse?.code?.let { viewModel.startCall(it) }
    }

    fun doBuyPoint() {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
        handleBuyPoint.buyPoint(
            childFragmentManager,
            bundle,
            object : BuyPointBottomFragment.HandleBuyPoint {
                override fun buyPointSucess() {
                    activity?.let { it1 ->
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


    private fun showDialogAlreadyFavorite() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.favorited_alert)
            .setTextPositiveButton(R.string.text_OK)
            .setOnPositivePressed {
                it.dismiss()
            }
    }

    private fun loadData() {
        consultantResponseLocal?.code?.let {
            viewModel.loadDetailUser(it)
            // check user is first chat
            viewModel.loadMailInfo(it)
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
        viewModel.statusRemoveFavorite.observe(viewLifecycleOwner, handleResuleStatusUnFavorite)
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
        consultantResponse?.let { performerResponse ->
            run {
                val fragment: Fragment? =
                    childFragmentManager.findFragmentByTag("CallConnectionDialog")
                val dialog: CallConnectionDialog
                if (fragment != null) {
                    dialog = fragment as CallConnectionDialog
                    if (it.result == RESULT_NG) {
                        dialog.setMessage(it.message, true)
                    } else {
                        dialog.setMessage(getString(R.string.call_content))
                        dialog.setCallingCancelListener(this@DetailUserProfileFragment)
                    }
                } else {
                    val message =
                        if (it.result == RESULT_NG) it.message else getString(R.string.call_content)
                    val isError = it.result == RESULT_NG
                    dialog = CallConnectionDialog.newInstance(performerResponse, message, isError)
                    dialog.show(childFragmentManager, "CallConnectionDialog")
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
                putInt(VIEW_STATUS, loginType)
            }
            appNavigation.openUserDetailToLiveStream(bundle)
        }
    }

    private val buttonEnableHandle: Observer<Boolean> = Observer {
        setButtonEnable(it)
    }

    private var handleResultUserGallery: Observer<List<GalleryResponse>?> = Observer {
        if (it != null && it.isNotEmpty()) {
            when {
                it.size <= 3 -> {
                    numberTimeCanScrollDown = 0
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
            galleryAdapter.submitList(it)
        } else {
            val itemNoImage =
                "android.resource://" + requireContext().packageName + "/" + R.drawable.default_avt_performer;
            val listNoGallery = listOf(
                GalleryResponse(
                    thumbnailImage = ThumbnailImageResponse(url = itemNoImage),
                    comment = ""
                ),
                GalleryResponse(
                    thumbnailImage = ThumbnailImageResponse(url = itemNoImage),
                    comment = ""
                ),
                GalleryResponse(
                    thumbnailImage = ThumbnailImageResponse(url = itemNoImage),
                    comment = ""
                )
            )
            galleryAdapter.submitList(listNoGallery)
            binding.ivArrowDown.visibility = GONE
        }
    }

    private var handleResultDetailUser: Observer<ConsultantResponse?> = Observer {
        if (it != null) {
            showDataUserProfile(it)
            setClickForDialogBlock(it)
            consultantResponse = it
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
            changeStatusIsFavorite(true)
        }
    }

    private var handleResuleStatusUnFavorite: Observer<Boolean> = Observer {
        if (it) {
            changeStatusIsFavorite(false)
        }
    }

    private var handleFirstChat: Observer<Boolean> = Observer {
        this.isFirstChat = it
    }

    private fun changeStatusIsFavorite(isFavorite: Boolean) {
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
                                    bundle.putString(BUNDLE_KEY.USER_PROFILE, user.code)
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

                val isWaiting = ConsultantResponse.isWaiting(user.callStatus, user.chatStatus)
                val isLiveStream =
                    ConsultantResponse.isLiveStream(user.callStatus, user.chatStatus)
                val isPrivateLiveStream =
                    ConsultantResponse.isPrivateLiveStream(user.callStatus, user.chatStatus)
                val isOffline = ConsultantResponse.isOffline(user.callStatus, user.chatStatus)

                val presenceStatusBgResId = when {
                    isWaiting -> R.drawable.bg_performer_status_waiting
                    isLiveStream -> R.drawable.bg_performer_status_live_streaming
                    isPrivateLiveStream -> R.drawable.bg_performer_status_private_delivery
                    else -> R.drawable.bg_performer_status_offline
                }

                val presenceStatusText = when {
                    isWaiting -> resources.getString(R.string.presence_status_waiting)
                    isLiveStream -> resources.getString(R.string.presence_status_live_streaming)
                    isPrivateLiveStream -> resources.getString(R.string.presence_status_private_delivery)
                    else -> resources.getString(R.string.presence_status_offline)
                }

                presenceStatusTv.setBackgroundResource(presenceStatusBgResId)
                presenceStatusTv.text = presenceStatusText

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

                if (isLiveStream) {
                    ivMessage.setImageResource(R.drawable.ic_message_small_detail)
                    tvLiveStreamCount.text =
                        (user.loginMemberCount + user.peepingMemberCount).toString()
                    llStatusViewerCount.visibility = VISIBLE
                } else {
                    ivMessage.setImageResource(R.drawable.ic_message_large_detail)
                    llStatusViewerCount.visibility = GONE
                }

                llCallConsult.visibility = if (isWaiting || isLiveStream) VISIBLE else GONE
                ivBallonPeep.visibility = if (isLiveStream) VISIBLE else GONE
                llPeep.visibility = if (isLiveStream) VISIBLE else GONE
                ivPrivateDelivery.visibility =
                    if (isPrivateLiveStream || isOffline) VISIBLE else GONE
                ivBallonLiveGl50.visibility = if (isWaiting) VISIBLE else GONE

                changeStatusIsFavorite(user.isFavorite)
                binding.imgRanking.setImageResource(
                    ConsultantResponse.getImageViewForRank(
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
        const val ACCEPTING = 1
        const val LEAVING = 0
        const val PREVIOUS = 0
        const val WEEK = 1
        const val MONTH = 2

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
            bundle.putSerializable(BUNDLE_KEY.USER_PROFILE, performer)
            detailUserProfileFragment.arguments = bundle
            return detailUserProfileFragment
        }
    }
}
