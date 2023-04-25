package jp.careapp.counseling.android.ui.live_stream

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.DeviceUtil.Companion.hideKeyBoardWhenClickOutSide
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.getHeight
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.network.socket.MaruCastManager
import jp.careapp.counseling.android.ui.live_stream.InformationPerformerBottomFragment.ClickItemView
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.PARTY
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.PREMIUM_PRIVATE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.PRIVATE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.TWO_SHOT_VALUE_0
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.TWO_SHOT_VALUE_2
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.UI_BUY_POINT
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.UI_DISMISS_PRIVATE_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel.Companion.UI_SHOW_WAITING_PRIVATE_MODE
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point.PurchasePointBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.camera_micrro_switch.CameraMicroSwitchBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.camera_micrro_switch.LiveStreamMicAndCameraChangeCallback
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheetDialogListener
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.ConnectPrivateBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.LiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.notice.LiveStreamNoticeBottomSheet
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.ROOT_SCREEN
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.PermissionUtils
import jp.careapp.counseling.android.utils.PermissionUtils.launchMultiplePermission
import jp.careapp.counseling.android.utils.PermissionUtils.registerPermission
import jp.careapp.counseling.android.utils.SocketInfo.RESULT_NG
import jp.careapp.counseling.android.utils.showSoftKeyboard
import jp.careapp.counseling.databinding.FragmentLiveStreamBinding
import org.marge.marucast_android_client.views.VideoRendererView
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LiveStreamFragment : BaseFragment<FragmentLiveStreamBinding, LiveStreamViewModel>(),
    LiveStreamConfirmBottomSheetDialogListener, LiveStreamConnectPrivateListener,
    MaruCastManager.SwitchViewerCallback, LiveStreamMicAndCameraChangeCallback {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    @Inject
    lateinit var maruCastManager: MaruCastManager

    override val layoutId: Int = R.layout.fragment_live_stream

    private val mViewModel: LiveStreamViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: LiveStreamAdapter? = null

    private var consultantResponse: ConsultantResponse? = null

    private var currentMode = LiveStreamMode.PARTY

    private var filter: IntentFilter? = null

    private var rootScreen: Int = 0

    private var earphoneEventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action ?: return
            if (Intent.ACTION_HEADSET_PLUG == action) {
                Timber.i("Intent.ACTION_HEADSET_PLUG")
                val state = intent.getIntExtra("state", -1)
                val mode =
                    if (state == 0) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_IN_CALL
                val isSpeakerphoneOn = state == 0
                mViewModel.setAudioConfig(mode, isSpeakerphoneOn)
            }
        }
    }

    private val cameraAndAudioPermissionLauncher =
        registerPermission { onCameraAndAudioPermissionResult(it) }

    private fun onCameraAndAudioPermissionResult(state: PermissionUtils.PermissionState) {
        when (state) {
            PermissionUtils.PermissionState.Denied -> {
                binding.btnVideoMic.isEnabled = false
            }
            PermissionUtils.PermissionState.Granted -> {
                showLiveStreamConfirmBottomSheet(PREMIUM_PRIVATE_MODE_REGISTER, this)
            }
            PermissionUtils.PermissionState.PermanentlyDenied -> {
                binding.btnVideoMic.isEnabled = false
            }
        }
    }

    private var isKeyboardShowing = false
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        binding.root.getWindowVisibleDisplayFrame(r)
        val screenHeight = binding.root.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            if (!isKeyboardShowing) {
                isKeyboardShowing = true
            }
        } else {
            if (isKeyboardShowing) {
                binding.memberCommentViewGroup.isVisible = false
                mViewModel.reloadMode()
                isKeyboardShowing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
    }

    override fun onStop() {
        super.onStop()

        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun initView() {
        super.initView()

        arguments?.let {
            mViewModel.setFlaxLoginAuthResponse(it.getSerializable(BUNDLE_KEY.FLAX_LOGIN_AUTH_RESPONSE) as FlaxLoginAuthResponse)
            val viewStatus = it.getInt(BUNDLE_KEY.VIEW_STATUS)
            currentMode = if (viewStatus == 1) LiveStreamMode.PEEP else LiveStreamMode.PARTY
            mViewModel.setViewerStatus(viewStatus)
            consultantResponse = it.getSerializable(BUNDLE_KEY.USER_PROFILE) as ConsultantResponse
            rootScreen = it.getInt(ROOT_SCREEN)
        }

        consultantResponse?.let {
            Glide.with(requireContext()).load(it.imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.default_avt_performer)
                        .circleCrop()
                )
                .into(binding.ivPerformer)
            binding.tvPerformerName.text = it.name
        }

        binding.viewModel = mViewModel

        binding.performerView.layoutParams.height =
            DeviceUtil.getScreenHeightWithNavigationBar(requireActivity())

        mAdapter = LiveStreamAdapter()
        binding.rcvCommentList.adapter = mAdapter

        mViewModel.handleConnect(requireActivity(), this)

        updateModeStatus()

        filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
        requireContext().registerReceiver(earphoneEventReceiver, filter)
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.ivPerformer.setOnClickListener {
            if (!isDoubleClick) {
                showPerformerInfoBottomSheet(object : ClickItemView {
                    override fun onAddFollowClick() {
                        consultantResponse?.isFavorite = true
                    }

                    override fun onRemoveFollowClick() {
                        consultantResponse?.isFavorite = false
                    }
                })
            }
        }

        binding.btnComment.setOnClickListener {
            if (!isDoubleClick) {
                binding.groupAllBtn.isVisible = false
                binding.memberCommentViewGroup.isVisible = true
                binding.btnSendComment.getHeight { binding.edtComment.minimumHeight = it }
                binding.edtComment.requestFocus()
                requireContext().showSoftKeyboard(binding.edtComment)
            }
        }

        hideKeyBoardWhenClickOutSide(
            binding.root,
            arrayListOf(binding.btnSendComment, binding.rcvCommentList),
            requireActivity()
        )

        binding.btnWhisper.setOnClickListener {
            if (!isDoubleClick) {
                val whisperList = mViewModel.whisperList.value ?: arrayListOf()
                SecretMessageBottomFragment.showSecretMessageBottomSheet(
                    whisperList,
                    childFragmentManager,
                    object : SecretMessageBottomFragment.ClickItemView {
                        override fun clickSend(sendMessage: String) {
                            mViewModel.sendWhisperMessage(sendMessage)
                        }
                    }
                )
            }
        }

        binding.btnPrivate.setOnClickListener {
            if (!isDoubleClick) {
                showLiveStreamConfirmBottomSheet(PRIVATE_MODE_REGISTER, this)
            }
        }

        binding.btnParty.setOnClickListener {
            if (!isDoubleClick) {
                showLiveStreamConfirmBottomSheet(CHANGE_TO_PARTY_MODE, this)
            }
        }

        binding.btnVideoMic.setOnClickListener {
            if (!isDoubleClick) {
                if (currentMode == LiveStreamMode.PRIVATE) {
                    cameraAndAudioPermissionLauncher.launchMultiplePermission(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                } else if (currentMode == LiveStreamMode.PREMIUM_PRIVATE) {
                    CameraMicroSwitchBottomSheet.newInstance(
                        mViewModel.isMicMute(),
                        mViewModel.isCameraMute(),
                        this
                    ).show(childFragmentManager, "CameraMicroSwitchBottomSheet")
                }
            }
        }

        binding.btnPoint.setOnClickListener {
            if (!isDoubleClick) {
                showPointPurchaseBottomSheet(Define.BUY_POINT_FIRST)
            }
        }

        binding.edtComment.addTextChangedListener {
            binding.btnSendComment.isEnabled = getInputComment().isNotBlank()
        }

        binding.btnSendComment.setOnClickListener {
            if (!isDoubleClick) {
                mViewModel.sendChatMessage(getInputComment())
                binding.edtComment.text?.clear()
            }
        }

        binding.btnClose.setOnClickListener { if (!isDoubleClick) showLogoutConfirm() }

        binding.btnCameraFlip.setOnClickListener {
            if (!isDoubleClick) {
                maruCastManager.switchCamera()
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.currentModeLiveData.observe(viewLifecycleOwner) {
            binding.groupAllBtn.isVisible = false
            when (it) {
                LiveStreamMode.PARTY -> {
                    binding.llItemParty.isVisible = true
                    binding.groupButtonPartyMode.isVisible = true
                }

                LiveStreamMode.PRIVATE -> {
                    binding.llItemPrivate.isVisible = true
                    binding.groupButtonPrivateMode.isVisible = true
                }

                LiveStreamMode.PREMIUM_PRIVATE -> {
                    binding.llItemPremiumPrivate.isVisible = true
                    binding.groupButtonPrivateMode.isVisible = true
                }

                LiveStreamMode.PEEP -> {
                    binding.llItemPeeping.isVisible = true
                    binding.groupButtonPeepingMode.isVisible = true
                }
            }
        }

        bindingConnectResult()
        bindingUpdateUIMode()
        bindingViewerType()
        bindingTwoShotHandle()
        bindingMessageHandle()
        bindingWhisperHandle()
        bindingPointCheckingHandle()
        bindingCurrentPointHandle()
    }

    private fun bindingConnectResult() {
        mViewModel.connectResult.observe(viewLifecycleOwner) {
            if (it.result == RESULT_NG) {
                when {
                    it.isLogout -> {
                        logout()
                    }
                    else -> {
                        showErrorDialog(it.message)
                    }
                }
            }
        }
    }

    private fun bindingUpdateUIMode() {
        mViewModel.updateUIMode.observe(viewLifecycleOwner) {
            when (it) {
                UI_DISMISS_PRIVATE_MODE -> {
                    dismissPrivateModeConnectionBottomSheet()
                }
                UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE -> {
                    dismissPrivateModeConnectionBottomSheet()
                    showPrivateModeDenied()
                }
                UI_SHOW_WAITING_PRIVATE_MODE -> {
                    showPrivateModeRequest()
                }
                UI_BUY_POINT -> {

                }
            }
        }
    }

    private fun bindingViewerType() {
        mViewModel.viewerStatus.observe(viewLifecycleOwner) {
            when {
                it == 1 -> currentMode = LiveStreamMode.PEEP
                mViewModel.twoShot.value == TWO_SHOT_VALUE_0 -> currentMode = LiveStreamMode.PARTY
                mViewModel.twoShot.value == TWO_SHOT_VALUE_2 -> currentMode = LiveStreamMode.PRIVATE
            }
        }
    }

    private fun bindingTwoShotHandle() {
        mViewModel.twoShot.observe(viewLifecycleOwner) {
            when {
                mViewModel.viewerStatus.value == 1 -> currentMode = LiveStreamMode.PEEP
                it == TWO_SHOT_VALUE_0 -> currentMode = LiveStreamMode.PARTY
                it == TWO_SHOT_VALUE_2 -> currentMode = LiveStreamMode.PRIVATE
            }
            updateModeStatus()
        }
    }

    private fun bindingMessageHandle() {
        mViewModel.messageList.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
            binding.rcvCommentList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
        }
    }

    private fun bindingWhisperHandle() {
        mViewModel.whisperList.observe(viewLifecycleOwner) {
            val fragment = childFragmentManager.findFragmentByTag("SecretMessageBottomFragment")
            if (fragment != null) {
                (fragment as SecretMessageBottomFragment).updateMessageList(it)
            }
        }
    }

    private fun bindingPointCheckingHandle() {
        mViewModel.pointState.observe(viewLifecycleOwner) {
            when (it) {
                PointState.PointUnder1000 -> {
                    showPointPurchaseBottomSheet(Define.INSU_POINT)
                }
                PointState.PointUnder500 -> {
                    showPointPurchaseBottomSheet(Define.BUY_POINT_UNDER_500)
                    mViewModel.endPointChecking()
                }
                else -> {}
            }
        }
    }

    private fun bindingCurrentPointHandle() {
        mViewModel.currentPoint.observe(viewLifecycleOwner) {
            rxPreferences.setPoint(it)
        }
    }

    private fun updateModeStatus() {
        when (currentMode) {
            LiveStreamMode.PEEP -> {
                binding.llItemPeeping.visibility = VISIBLE
                binding.llItemParty.visibility = GONE
                binding.llItemPrivate.visibility = GONE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.btnParty.visibility = VISIBLE
                binding.btnPrivate.visibility = GONE
                binding.btnComment.visibility = GONE
                binding.btnVideoMic.visibility = GONE
            }
            LiveStreamMode.PARTY -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemParty.visibility = VISIBLE
                binding.llItemPrivate.visibility = GONE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.btnParty.visibility = GONE
                binding.btnPrivate.visibility = VISIBLE
                binding.btnComment.visibility = VISIBLE
                binding.btnVideoMic.visibility = GONE
            }
            LiveStreamMode.PRIVATE -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemParty.visibility = GONE
                binding.llItemPrivate.visibility = VISIBLE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.btnParty.visibility = VISIBLE
                binding.btnPrivate.visibility = GONE
                binding.btnComment.visibility = VISIBLE
                binding.btnWhisper.visibility = GONE
                binding.btnVideoMic.visibility = VISIBLE
            }
            LiveStreamMode.PREMIUM_PRIVATE -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemParty.visibility = GONE
                binding.llItemPrivate.visibility = GONE
                binding.llItemPremiumPrivate.visibility = VISIBLE
                binding.btnParty.visibility = VISIBLE
                binding.btnPrivate.visibility = GONE
                binding.btnComment.visibility = VISIBLE
                binding.btnWhisper.visibility = GONE
                binding.btnVideoMic.visibility = VISIBLE
            }
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(errorMessage)
            .setTextOkButton(R.string.close)
            .setOnOkButtonBackground(R.drawable.bg_cancel_btn)
            .setOnOkButtonPressed {
                it.dismiss()
            }
    }

    private fun showPrivateModeDenied() {
        val bottomSheet = LiveStreamNoticeBottomSheet()
        bottomSheet.show(childFragmentManager, "LiveStreamNoticeBottomSheet")
    }

    private fun showLiveStreamConfirmBottomSheet(
        mode: String,
        listener: LiveStreamConfirmBottomSheetDialogListener
    ) {
        val bottomSheet = LiveStreamConfirmBottomSheet.newInstance(mode, listener)
        bottomSheet.show(childFragmentManager, "LiveStreamConfirmBottomSheet")
    }

    private fun showPerformerInfoBottomSheet(itemView: ClickItemView) {
        InformationPerformerBottomFragment.showInfoPerformerBottomSheet(
            childFragmentManager,
            itemView,
            consultantResponse!!
        )
    }

    private fun logout() {
        val bundle = Bundle().apply {
            putSerializable(BUNDLE_KEY.USER_PROFILE, consultantResponse)
            putInt(ROOT_SCREEN, rootScreen)
        }
        appNavigation.openLiveStreamToExitLiveStream(bundle)
        mViewModel.logout()
    }

    private fun showLogoutConfirm() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.logout_confirm)
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed {
                it.dismiss()
                logout()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showPointPurchaseBottomSheet(typeBuyPoint: Int) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.TYPE_BUY_POINT, typeBuyPoint)
        handleBuyPoint.buyPointLiveStream(
            childFragmentManager, bundle,
            object : PurchasePointBottomSheet.PurchasePointCallback {
                override fun onPointItemClick(point: Int, money: Int) {
                    val purchasePointUrl = buildString {
                        append(Define.URL_LIVE_STREAM_POINT_PURCHASE)
                        append("?token=${rxPreferences.getToken()}")
                        append("&&point=${point}")
                        append("&money=${money}")
                    }
                    val arguments = Bundle().apply {
                        putString(Define.URL_WEB_VIEW, purchasePointUrl)
                    }
                    appNavigation.openLiveStreamBuyPointCredit(arguments)
                }
            }
        )
    }

    private fun showPrivateModeRequest() {
        ConnectPrivateBottomSheet.newInstance(object : LiveStreamConnectPrivateListener {
            override fun onClickButtonCancelConnectPrivate() {
                mViewModel.privateModeCancel()
            }

        }).show(childFragmentManager, "ConnectPrivateBottomSheet")
    }

    private fun dismissPrivateModeConnectionBottomSheet() {
        val fragment: Fragment? =
            childFragmentManager.findFragmentByTag("ConnectPrivateBottomSheet")
        if (fragment != null) (fragment as ConnectPrivateBottomSheet).dismiss()
    }

    override fun onClickButtonOKConfirmBottomSheet(mode: String) {
        when (mode) {
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                mViewModel.updateMode(PREMIUM_PRIVATE)
                currentMode = LiveStreamMode.PREMIUM_PRIVATE
                updateModeStatus()
            }
            PRIVATE_MODE_REGISTER -> {
                mViewModel.updateMode(PRIVATE)
            }
            CHANGE_TO_PARTY_MODE -> {
                mViewModel.updateMode(PARTY)
            }
            PERFORMER_OUT_CONFIRM -> {}
        }
    }

    override fun onClickButtonCancelConnectPrivate() {
        // TODO Do nothing
    }

    private fun getInputComment() = binding.edtComment.text?.trim().toString()

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(earphoneEventReceiver)
    }

    override fun onSwitchViewerGroupVisible(isVisible: Boolean) {
        binding.memberCameraViewGroup.visibility = if (isVisible) VISIBLE else GONE
    }

    override fun getPresenterView(): VideoRendererView {
        return binding.performerView
    }

    override fun getViewerView(): VideoRendererView {
        return binding.memberViewCamera
    }

    override fun onMicChange(_isMicMute: Boolean) {
        mViewModel.updateMicSetting(_isMicMute)
    }

    override fun onCameraChange(_isCameraMute: Boolean) {
        mViewModel.updateCameraSetting(_isCameraMute)
    }
}