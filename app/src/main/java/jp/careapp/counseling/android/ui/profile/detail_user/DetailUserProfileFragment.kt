package jp.careapp.counseling.android.ui.profile.detail_user

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.GalleryAdapter
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.GalleryResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.calling.CallingViewModel
import jp.careapp.counseling.android.ui.profile.block_report.BlockAndReportBottomFragment
import jp.careapp.counseling.android.ui.profile.tab_review.TabReviewFragment
import jp.careapp.counseling.android.ui.profile.tab_user_info_detail.TabDetailUserProfileFragment
import jp.careapp.counseling.android.ui.profile.update_trouble_sheet.TroubleSheetUpdateFragment
import jp.careapp.counseling.android.utils.*
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.android.utils.extensions.hasPermissions
import jp.careapp.counseling.databinding.FragmentDetailUserProfileBinding
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserProfileFragment :
    BaseFragment<FragmentDetailUserProfileBinding, DetailUserProfileViewModel>() {

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
    // user laod from server
    private var consultantResponse: ConsultantResponse? = null

    // user in list user from local
    private var consultantResponseLocal: ConsultantResponse? = null

    private var userInforTabAdapter: UserInforTabAdapter? = null
    private var tabDetailUserProfileFragment: TabDetailUserProfileFragment? = null
    private var tabReviewFragment: TabReviewFragment? = null
    private var isFirstChat: Boolean? = null
    private var previousScreen = ""
    private var position: Int = 0

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
                galleryAdapter = GalleryAdapter(requireContext(),binding.avatarIv.height)
                binding.rvGallery.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                binding.rvGallery.adapter = galleryAdapter
                Log.d("wgawgawg", "onGlobalLayout: ${binding.avatarIv.height}")
            }

        })

    }
    fun getScreenWidth(): Int {
        Log.d("rgargaewrh", "getScreenWidth: ${binding.avatarIv.height}")

        return binding.avatarIv.height / 2
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
        binding.sendConsultLl.setOnClickListener {
            if (!isDoubleClick) {
                openChatScreen()
            }
        }

        binding.ivArrowDown.setOnClickListener {
            val layoutManager = binding.rvGallery.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findLastVisibleItemPosition()
            Log.d("egeaweah", "setOnClick: ${galleryAdapter.itemCount}")
            Log.d("egeaweah", "setOnClick: $currentPosition")
            binding.rvGallery.smoothScrollToPosition(currentPosition+3)
        }

        binding.llCallConsult.setOnClickListener {
            if (!isDoubleClick) {
                if (hasPermissions(arrayOf(RECORD_AUDIO))) {
                    checkPoint()
                } else if (shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
                    showDialogNeedMicrophonePermission()
                } else {
                    showDialogRequestMicrophonePermission()
                }
            }
        }

        // user offline
//        binding.sendNotiLl.setOnClickListener {
//            if (!isDoubleClick) {
//                if (isFirstChat == null) {
//                    consultantResponseLocal?.code?.let { it1 ->
//                        viewModel.loadMailInfo(
//                            it1
//                        )
//                    }
//                } else {
//                    isFirstChat?.let {
//                        if (it) {
//                            showDialogFavorite()
//                        } else {
//                            if ((rxPreferences.getPoint() == 0)) {
//                                doBuyPoint()
//                            } else {
//                                handleOpenChatScreen()
//                            }
//                        }
//                    }
//                }
//            }
//        }
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
                    var bundle = Bundle()
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
                    appNavigation.openDetailUserToTroubleSheetUpdate(bundle)
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
        if ((rxPreferences.getPoint() < 1000)) {
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
            .setContent(R.string.msg_title_request_buy_point)
            .setTextPositiveButton(R.string.buy_point)
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

    private fun showDialogConfirmCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(R.string.msg_confirm_call)
            .setTextPositiveButton(R.string.confirm_call)
            .setTextNegativeButton(R.string.send_free_mess)
            .setOnPositivePressed {
                it.dismiss()
                if (callingViewModel.isCalling()) {
                    showDialogWarningDuringCall()
                } else {
                    openCalling()
                }
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
        Bundle().also {
            it.putString(BUNDLE_KEY.PERFORMER_NAME, consultantResponse?.name ?: "")
            it.putString(BUNDLE_KEY.PERFORMER_CODE, consultantResponse?.code ?: "")
            it.putString(BUNDLE_KEY.PERFORMER_IMAGE, consultantResponse?.imageUrl ?: "")
        }.let {
            appNavigation.openToCalling(it)
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
                    activity?.let { it1 ->
                        handleOpenChatScreen()
                    }
                }
            }
        )
    }

    private fun handleOpenChatScreen(isShowFreeMess: Boolean = false) {
        var bundle = Bundle()
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

    private fun showDialogFavorite() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.title_favorite_alert)
            .setContent(R.string.sub_title_favorite_alert)
            .setTextNegativeButton(R.string.cancel_favorite_alert)
            .setTextPositiveButton(R.string.confirm_favorite_alert)
            .setOnNegativePressed {
                it.dismiss()
            }
            .setOnPositivePressed {
                if (!isDoubleClick) {
                    if (binding.removeFavoriteTv.isVisible) {
                        it.dismiss()
                        showDialogAlreadyFavorite()
                    } else {
                        consultantResponseLocal?.code?.let { it1 ->
                            isShowFromUserDisable = true
                            viewModel.addUserToFavorite(
                                it1
                            )
                            it.dismiss()
                        }
                    }
                }
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

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.userProfileResult.observe(viewLifecycleOwner, handleResultDetailUser)
        viewModel.statusFavorite.observe(viewLifecycleOwner, handleResultStatusFavorite)
        viewModel.statusRemoveFavorite.observe(viewLifecycleOwner, handleResuleStatusUnFavorite)
        viewModel.isFirstChat.observe(viewLifecycleOwner, handleFirstChat)
        viewModel.blockUserResult.observe(viewLifecycleOwner
            , handleBlockResult)
        viewModel.userGallery.observe(viewLifecycleOwner, handleResultUserGallery)


    }

    private var handleResultUserGallery: Observer<List<GalleryResponse>?> = Observer {
        if (it != null) {
            val abc = it+it+it
            galleryAdapter.submitList(abc)
        } else {
            Log.d("aerharhrh", "asasga: null r")
//            consultantResponse = consultantResponseLocal
//            if (consultantResponseLocal != null) {
//                showDataUserProfile(consultantResponseLocal!!)
//                setClickForDialogBlock(consultantResponseLocal!!)
//            }
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

    private fun setClickForDialogBlock(consultantResponse: ConsultantResponse){
        consultantResponse.also { user ->
            binding.ivMore.setOnClickListener {
                if (!isDoubleClick) {
                    BlockAndReportBottomFragment.showBlockAndReportBottomSheet(
                        parentFragmentManager,
                        object : BlockAndReportBottomFragment.ClickItemView {
                            override fun clickBlock() {
                                if (!isDoubleClick) {
                                    user.let { data ->
                                        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
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
                if (user.callStatus == CallStatus.ONLINE && user.chatStatus == ChatStatus.OFFLINE) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_offline)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_offline)
                } else if (user.callStatus == CallStatus.INCOMING_CALL && user.chatStatus == ChatStatus.OFFLINE) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_offline)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_offline)
                } else if (user.callStatus == CallStatus.OFFLINE && user.chatStatus == ChatStatus.WAITING) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_live_streaming)
                } else if (user.callStatus == CallStatus.OFFLINE && user.chatStatus == ChatStatus.CHATTING) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_live_streaming)
                } else if (user.callStatus == CallStatus.OFFLINE && user.chatStatus == ChatStatus.TWO_SHOT_CHAT) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_private_delivery)
                } else if (user.callStatus == CallStatus.OFFLINE && user.chatStatus == ChatStatus.OFFLINE) {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_waiting)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_waiting)
                } else {
                    presenceStatusTv.setBackgroundResource(R.drawable.bg_performer_status_offline)
                    presenceStatusTv.text =
                        resources.getString(R.string.presence_status_offline)
                }
                when (user.stage) {
                    SILVER -> {
                        stageIv.setImageResource(R.drawable.ic_silver_home)
                    }
                    GOLD -> {
                        stageIv.setImageResource(R.drawable.ic_gold_home)
                    }
                    PLATINUM -> {
                        stageIv.setImageResource(R.drawable.ic_platinum_home)
                    }
                    BRONZE -> {
                        stageIv.setImageResource(R.drawable.ic_bronze_home)
                    }
                    DIAMOND -> {
                        stageIv.setImageResource(R.drawable.ic_diamond_home)
                    }
                    else -> {
                        stageIv.setImageResource(R.drawable.ic_basic_home)
                    }
                }

                if (user.profilePattern == 1) {
                    avatarIv.visibility = VISIBLE
                    avatarIv.loadImage(user.imageUrl, R.drawable.ic_avatar_default, false)
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

                tvMessageNotice.text = user.messageNotice

//                tvMessageOfTheDay.text = user.messageOfTheDay

                tvAge.text = user.age.toString() + resources.getString(R.string.age_raw)

                userNameTv.text = user.name

                var isWaiting = false
                var isLiveStream = false
                var isPrivateLiveStream = false
                var isOnline = false
                if (user.callStatus == 1 && user.chatStatus == 0) {
                    isWaiting = true
                } else if (user.callStatus == 2 && user.chatStatus == 0) {
                    isWaiting = true
                } else if (user.callStatus == 0 && user.chatStatus == 1) {
                    isLiveStream = true
                } else if (user.callStatus == 0 && user.chatStatus == 2) {
                    isLiveStream = true
                } else if (user.callStatus == 0 && user.chatStatus == 3) {
                    isPrivateLiveStream = true
                } else {
                    isOnline = true
                }

                if (isWaiting || isLiveStream) {
                    llCallConsult.visibility = VISIBLE
                } else {
                    llCallConsult.visibility = GONE
                }
                if (isWaiting) {
                    ivBallonLive.visibility = VISIBLE
                } else {
                    ivBallonLive.visibility = GONE
                }
                if (isLiveStream) {
                    llPrivateDelivery.visibility = VISIBLE
                    ivBallonPeep.visibility = VISIBLE
                    llPeep.visibility = VISIBLE
                } else {
                    llPrivateDelivery.visibility = GONE
                    ivBallonPeep.visibility = GONE
                    llPeep.visibility = GONE
                }
                changeStatusIsFavorite(user.isFavorite)

                user.ranking?.let {
                    imgRanking.visibility = if (it.ranking in 1..30) VISIBLE else INVISIBLE
                    when (it.interval) {
                        PREVIOUS -> {
                            when (it.ranking) {
                                1 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_1)
                                2 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_2)
                                3 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_3)
                                4 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_4)
                                5 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_5)
                                6 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_6)
                                7 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_7)
                                8 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_8)
                                9 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_9)
                                10 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_10)
                                11 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_11)
                                12 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_12)
                                13 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_13)
                                14 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_14)
                                15 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_15)
                                16 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_16)
                                17 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_17)
                                18 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_18)
                                19 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_19)
                                20 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_20)
                                21 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_21)
                                22 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_22)
                                23 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_23)
                                24 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_24)
                                25 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_25)
                                26 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_26)
                                27 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_27)
                                28 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_28)
                                29 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_29)
                                30 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_daily_30)
                            }
                        }
                        WEEK -> {
                            when (it.ranking) {
                                1 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_1)
                                2 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_2)
                                3 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_3)
                                4 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_4)
                                5 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_5)
                                6 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_6)
                                7 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_7)
                                8 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_8)
                                9 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_9)
                                10 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_10)
                                11 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_11)
                                12 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_12)
                                13 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_13)
                                14 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_14)
                                15 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_15)
                                16 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_16)
                                17 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_17)
                                18 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_18)
                                19 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_19)
                                20 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_20)
                                21 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_21)
                                22 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_22)
                                23 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_23)
                                24 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_24)
                                25 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_25)
                                26 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_26)
                                27 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_27)
                                28 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_28)
                                29 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_29)
                                30 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_weekly_30)
                            }
                        }
                        else -> {
                            when (it.ranking) {
                                1 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_1)
                                2 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_2)
                                3 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_3)
                                4 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_4)
                                5 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_5)
                                6 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_6)
                                7 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_7)
                                8 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_8)
                                9 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_9)
                                10 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_10)
                                11 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_11)
                                12 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_12)
                                13 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_13)
                                14 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_14)
                                15 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_15)
                                16 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_16)
                                17 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_17)
                                18 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_18)
                                19 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_19)
                                20 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_20)
                                21 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_21)
                                22 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_22)
                                23 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_23)
                                24 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_24)
                                25 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_25)
                                26 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_26)
                                27 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_27)
                                28 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_28)
                                29 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_29)
                                30 -> imgRanking.setBackgroundResource(R.drawable.ic_rank_monthly_30)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val BASIC = 1
        const val SILVER = 2
        const val GOLD = 3
        const val PLATINUM = 4
        const val BRONZE = 5
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
            var detailUserProfileFragment = DetailUserProfileFragment()
            var bundle = Bundle()
            bundle.putString(BUNDLE_KEY.SCREEN_TYPE, previousScreen)
            val performer =
                if (listPerformer.size > position) listPerformer.get(position) else null
            bundle.putSerializable(BUNDLE_KEY.USER_PROFILE, performer)
            detailUserProfileFragment.arguments = bundle
            return detailUserProfileFragment
        }
    }
}
