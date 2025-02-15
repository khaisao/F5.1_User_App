package jp.slapp.android.android.ui.live_stream

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
import android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.DeviceUtil.Companion.hideKeyBoardWhenClickOutSide
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.getHeight
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.FlaxLoginAuthResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.handle.HandleBuyPoint
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.network.socket.MaruCastManager
import jp.slapp.android.android.ui.live_stream.DialogTag.CAMERA_MICRO_SWITCH_BOTTOM_SHEET
import jp.slapp.android.android.ui.live_stream.InformationPerformerBottomFragment.ClickItemView
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE_FROM_PEEP
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.PARTY
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.PREMIUM_PRIVATE
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.PRIVATE
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.TWO_SHOT_VALUE_0
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.TWO_SHOT_VALUE_2
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.UI_BUY_POINT
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.UI_DISMISS_PRIVATE_MODE
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE
import jp.slapp.android.android.ui.live_stream.LiveStreamViewModel.Companion.UI_SHOW_WAITING_PRIVATE_MODE
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.buy_point.PurchasePointBottomSheet
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheet
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheetDialogListener
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.connect_private.ConnectPrivateBottomSheet
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.connect_private.LiveStreamConnectPrivateListener
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.notice.LiveStreamNoticeBottomSheet
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.BUNDLE_KEY.Companion.ROOT_SCREEN
import jp.slapp.android.android.utils.Define
import jp.slapp.android.android.utils.PermissionUtils
import jp.slapp.android.android.utils.PermissionUtils.allPermissionGranted
import jp.slapp.android.android.utils.PermissionUtils.launchMultiplePermission
import jp.slapp.android.android.utils.PermissionUtils.registerPermission
import jp.slapp.android.android.utils.SocketInfo.RESULT_NG
import jp.slapp.android.android.utils.showSoftKeyboard
import jp.slapp.android.databinding.FragmentLiveStreamBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.marge.marucast_android_client.views.VideoRendererView
import timber.log.Timber
import java.util.Arrays
import javax.inject.Inject

@AndroidEntryPoint
class LiveStreamFragment : BaseFragment<FragmentLiveStreamBinding, LiveStreamViewModel>(),
    LiveStreamConfirmBottomSheetDialogListener, LiveStreamConnectPrivateListener,
    MaruCastManager.SwitchViewerCallback {

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

    private var xDown = 0f
    private var yDown = 0f

    private val cameraResponseTime = 2000L

    @Inject
    lateinit var audioManager: AudioManager
    private var firstTimeCheckBluetooth = true
    private var isWiredEarphoneConnected = false
    private var isBluetoothEarphoneConnected = false
    private var lastTypeHeadPhoneConnected: TypeHeadPhone = TypeHeadPhone.Speaker

    private var earphoneEventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            if (firstTimeCheckBluetooth) {
                isBluetoothEarphoneConnected =
                    Arrays.stream(audioManager.getDevices(GET_DEVICES_OUTPUTS))
                        .anyMatch { info -> info.getType() === TYPE_BLUETOOTH_SCO || info.getType() === TYPE_BLUETOOTH_A2DP }
                firstTimeCheckBluetooth = false
                if (isBluetoothEarphoneConnected) {
                    lastTypeHeadPhoneConnected = TypeHeadPhone.Bluetooth
                }
            }
            when (action) {
                Intent.ACTION_HEADSET_PLUG -> {
                    Timber.i("Intent.ACTION_HEADSET_PLUG")
                    val state = intent.getIntExtra("state", -1)
                    if (state == 1) {
                        isWiredEarphoneConnected = true
                        lastTypeHeadPhoneConnected = TypeHeadPhone.Wired
                    } else if (state == 0) {
                        isWiredEarphoneConnected = false
                    }
                    updateAudioConfig()
                }

                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(
                        BluetoothHeadset.EXTRA_STATE,
                        BluetoothHeadset.STATE_DISCONNECTED
                    )
                    if (state == BluetoothHeadset.STATE_CONNECTED) {
                        isBluetoothEarphoneConnected = true
                        lastTypeHeadPhoneConnected = TypeHeadPhone.Bluetooth
                    } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                        isBluetoothEarphoneConnected = false
                    }
                    updateAudioConfig()
                }
            }
        }
    }

    private fun updateAudioConfig() {
        if (isWiredEarphoneConnected && isBluetoothEarphoneConnected) {
            if (lastTypeHeadPhoneConnected == TypeHeadPhone.Wired) {
                mViewModel.setAudioConfig(
                    isSpeakerPhoneOn = false,
                    isBluetoothOn = false
                )
            }
            if (lastTypeHeadPhoneConnected == TypeHeadPhone.Bluetooth) {
                mViewModel.setAudioConfig(
                    isSpeakerPhoneOn = false,
                    isBluetoothOn = true
                )
            }

        } else if (isWiredEarphoneConnected) {
            mViewModel.setAudioConfig(
                isSpeakerPhoneOn = false,
                isBluetoothOn = false
            )
        } else if (isBluetoothEarphoneConnected) {
            mViewModel.setAudioConfig(
                isSpeakerPhoneOn = false,
                isBluetoothOn = true
            )
        } else {
            mViewModel.setAudioConfig(
                isSpeakerPhoneOn = true,
                isBluetoothOn = false
            )
        }
    }

    private val cameraAndAudioPermissionLauncher =
        registerPermission { onCameraAndAudioPermissionResult(it) }

    private fun onCameraAndAudioPermissionResult(state: PermissionUtils.PermissionState) {
        when (state) {
            PermissionUtils.PermissionState.Denied -> {
                binding.btnMicLiveStream.isEnabled = false
            }

            PermissionUtils.PermissionState.Granted -> {
            }

            PermissionUtils.PermissionState.PermanentlyDenied -> {
                binding.btnMicLiveStream.isEnabled = false
            }
        }
    }

    private var isKeyboardShowing = false
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        try {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    binding.rcvCommentList.apply {
                        updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomToTop = binding.edtComment.id
                        }
                    }
                    binding.viewHideAllButton.isVisible = false
                }
            } else {
                if (isKeyboardShowing) {
                    binding.rcvCommentList.apply {
                        updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomToTop = binding.btnEmpty.id
                        }
                    }
                    binding.memberCommentViewGroup.isVisible = false
                    isKeyboardShowing = false
                    binding.viewHideAllButton.isVisible = true
                    updateModeStatus(true)
                }
            }
        } catch (_: Exception) {

        }
    }

    private val listAlertDialogShowing = arrayListOf<CommonAlertDialog>()

    override fun onResume() {
        super.onResume()
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
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

        mViewModel.handleConnect(binding.performerView, this)

        updateModeStatus()

        filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        }
        requireContext().registerReceiver(earphoneEventReceiver, filter)

        handleBackPress()
    }

    @SuppressLint("ClickableViewAccessibility")
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

                /** Handle camera view to initialize position*/
                binding.memberViewCamera.x =  binding.memberViewCameraEmpty.x
                binding.memberViewCamera.y = binding.memberViewCameraEmpty.y
                binding.btnCameraFlip.x = binding.btnCameraFlipEmpty.x
                binding.btnCameraFlip.y = binding.btnCameraFlipEmpty.y
            }
        }

        hideKeyBoardWhenClickOutSide(
            binding.root,
            arrayListOf(binding.btnSendComment, binding.rcvCommentList),
            requireActivity()
        )

        binding.viewHideAllButton.setOnClickListener {
            binding.mainScrollView.isVisible = false
            binding.groupPerformerInfo.visibility = INVISIBLE
            binding.viewShowAllButton.isVisible = true
        }

        binding.viewShowAllButton.setOnClickListener {
            binding.mainScrollView.isVisible = true
            binding.groupPerformerInfo.visibility = VISIBLE
            binding.viewShowAllButton.isVisible = false
        }

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

        binding.btnMicLiveStream.setOnClickListener {
            if (!isDoubleClick) {
                cameraAndAudioPermissionLauncher.launchMultiplePermission(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                    )
                )
                if (requireContext().allPermissionGranted(
                        arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        )
                    )
                ) {
                    if (currentMode == LiveStreamMode.PARTY) {
                        if (mViewModel.twoShot.value == LiveStreamViewModel.TWO_SHOT_VALUE_11) {
                            showErrorDialog(getString(R.string.other_member_requested))
                        } else {
                            showLiveStreamConfirmBottomSheet(PRIVATE_MODE_REGISTER, this)
                        }
                    } else {
                        mViewModel.updateMicSetting(!mViewModel.isMicMute())
                        changeUiOfMicIcon()
                    }
                }
            }
        }

        binding.btnParty.setOnClickListener {
            if (!isDoubleClick) {
                if (currentMode == LiveStreamMode.PEEP) {
                    showLiveStreamConfirmBottomSheet(CHANGE_TO_PARTY_MODE_FROM_PEEP, this)
                } else {
                    showLiveStreamConfirmBottomSheet(CHANGE_TO_PARTY_MODE, this)
                }
            }
        }

        binding.btnCameraLiveStream.setOnClickListener {
            if (!isDoubleClick) {
                if (currentMode == LiveStreamMode.PRIVATE) {
                    showLiveStreamConfirmBottomSheet(PREMIUM_PRIVATE_MODE_REGISTER, this)
                } else if (currentMode == LiveStreamMode.PREMIUM_PRIVATE) {
                    mViewModel.updateCameraSetting(!mViewModel.isCameraMute())
                    changeUiOfCameraIcon()
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

        binding.memberViewCamera.setOnTouchListener { v, event ->
            v?.let {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        xDown = event.x
                        yDown = event.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val moveX = event.x
                        val moveY = event.y

                        val distanceX = moveX - xDown
                        val distanceY = moveY - yDown

                        val newCameraX: Float = when {
                            it.x + distanceX > resources.displayMetrics.widthPixels - it.width -> (resources.displayMetrics.widthPixels - it.width).toFloat()
                            it.x + distanceX > 0 -> it.x + distanceX
                            else -> 0f
                        }
                        val newCameraY: Float = when {
                            it.y + distanceY > resources.displayMetrics.heightPixels - it.height -> (resources.displayMetrics.heightPixels - it.height).toFloat()
                            it.y + distanceY > 0 -> it.y + distanceY
                            else -> 0f
                        }
                        binding.btnCameraFlip.x = binding.btnCameraFlip.x + (newCameraX - it.x)
                        binding.btnCameraFlip.y = binding.btnCameraFlip.y + (newCameraY - it.y)
                        it.x = newCameraX
                        it.y = newCameraY
                    }
                }
            }
            true
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        bindingConnectResult()
        bindingUpdateUIMode()
        bindingViewerType()
        bindingTwoShotHandle()
        bindingMessageHandle()
        bindingWhisperHandle()
        bindingPointCheckingHandle()
        bindingCurrentPointHandle()
        bindingMemberCountHandle()
    }

    private fun bindingMemberCountHandle() {
        mViewModel.memberCount.observe(viewLifecycleOwner) {
            binding.tvChatMemberNum.text = it
        }

        mViewModel.peepingCount.observe(viewLifecycleOwner) {
            binding.tvPeepMemberNum.text = it
        }
    }

    private fun changeUiOfMicIcon() {
        val isMicMute = mViewModel.isMicMute()
        if (currentMode == LiveStreamMode.PARTY) {
            binding.btnMicLiveStream.loadImage(R.drawable.ic_mic_off_white)
            binding.btnMicLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream_gradient)
        } else {
            if (isMicMute) {
                binding.btnMicLiveStream.loadImage(R.drawable.ic_mic_on_white)
                binding.btnMicLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream)
            } else {
                binding.btnMicLiveStream.loadImage(R.drawable.ic_mic_off_white)
                binding.btnMicLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream)
            }
        }
    }

    private fun changeUiOfCameraIcon() {
        val isCameraMute = mViewModel.isCameraMute()
        if (currentMode == LiveStreamMode.PRIVATE) {
            binding.btnCameraLiveStream.loadImage(R.drawable.ic_camera_off_white)
            binding.btnCameraLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream_gradient)
        } else {
            if (isCameraMute) {
                binding.btnCameraLiveStream.loadImage(R.drawable.ic_camera_on_white)
                binding.btnCameraLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream)
                if (currentMode == LiveStreamMode.PREMIUM_PRIVATE) {
                    binding.memberCameraViewGroup.visibility = VISIBLE
                }
            } else {
                binding.btnCameraLiveStream.loadImage(R.drawable.ic_camera_off_white)
                binding.btnCameraLiveStream.setBackgroundResource(R.drawable.bg_btn_live_stream)
                binding.memberCameraViewGroup.visibility = INVISIBLE
            }
        }
    }

    private fun bindingConnectResult() {
        mViewModel.connectResult.observe(viewLifecycleOwner) {
            if (it.result == RESULT_NG) {
                showErrorDialog(it.message, it.isLogout)
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
            updateModeStatus()
        }
    }

    private fun bindingTwoShotHandle() {
        mViewModel.twoShot.observe(viewLifecycleOwner) {
            when {
                mViewModel.viewerStatus.value == 1 -> currentMode = LiveStreamMode.PEEP
                it == TWO_SHOT_VALUE_0 -> currentMode = LiveStreamMode.PARTY
                it == TWO_SHOT_VALUE_2 -> {
                    currentMode = LiveStreamMode.PRIVATE
                    binding.tvChatMemberNum.text = 1.toString()
                    binding.tvPeepMemberNum.text = 0.toString()
                }
            }
            updateModeStatus()
        }
    }

    private fun bindingMessageHandle() {
        mViewModel.messageList.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
            binding.rcvCommentList.smoothScrollToPosition(mAdapter?.itemCount?.minus(0) ?: 0)
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

    private fun handleBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLogoutConfirm()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun updateModeStatus(isOnlyUpdateUI: Boolean = false) {
        when (currentMode) {
            LiveStreamMode.PEEP -> {
                binding.llItemPeeping.visibility = VISIBLE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.llItemPrivate.visibility = GONE
                binding.llItemParty.visibility = GONE
                binding.groupAllBtn.visibility = GONE
                binding.groupButtonPeepingMode.visibility = VISIBLE
            }

            LiveStreamMode.PARTY -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.llItemPrivate.visibility = GONE
                binding.llItemParty.visibility = VISIBLE
                binding.groupAllBtn.visibility = GONE
                binding.groupButtonPartyMode.visibility = VISIBLE
                dismissBottomSheet(CAMERA_MICRO_SWITCH_BOTTOM_SHEET)
                dismissBottomSheet(PREMIUM_PRIVATE_MODE_REGISTER)
                dismissBottomSheet(CHANGE_TO_PARTY_MODE)
                if (!isOnlyUpdateUI) {
                    mViewModel.resetCameraAndMic()
                    changeUiOfCameraIcon()
                    changeUiOfMicIcon()
                }
            }

            LiveStreamMode.PRIVATE -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemPremiumPrivate.visibility = GONE
                binding.llItemParty.visibility = GONE
                binding.llItemPrivate.visibility = VISIBLE
                binding.groupAllBtn.visibility = GONE
                binding.groupButtonPrivateMode.visibility = VISIBLE
                binding.memberCameraViewGroup.visibility = GONE
                if (!isOnlyUpdateUI) {
                    changeUiOfCameraIcon()
                    changeUiOfMicIcon()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(cameraResponseTime)
                        mViewModel.updateCameraSetting(true)
                        mViewModel.updateMicSetting(false)
                    }
                }
            }

            LiveStreamMode.PREMIUM_PRIVATE -> {
                binding.llItemPeeping.visibility = GONE
                binding.llItemParty.visibility = GONE
                binding.llItemPrivate.visibility = GONE
                binding.llItemPremiumPrivate.visibility = VISIBLE
                binding.groupAllBtn.visibility = GONE
                binding.groupButtonPrivateMode.visibility = VISIBLE
                binding.memberCameraViewGroup.visibility = VISIBLE
                if (!isOnlyUpdateUI) {
                    mViewModel.updateCameraSetting(false)
                    changeUiOfCameraIcon()
                }
            }
        }
    }

    private fun showErrorDialog(errorMessage: String, isLogout: Boolean = false) {
        if (isLogout) {
            dismissAllDialogShowing()
        }
        val dialog = CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(errorMessage)
            .setTextOkButton(R.string.close)
            .setOnOkButtonBackground(R.drawable.bg_cancel_btn)
            .setOnOkButtonPressed {
                if (isLogout) {
                    logout()
                } else {
                    it.dismiss()
                }
            }
        listAlertDialogShowing.add(dialog)
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
        bottomSheet.show(childFragmentManager, mode)
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
    }

    private fun showLogoutConfirm() {
        val dialog = CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.logout_confirm)
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed {
                it.dismiss()
                val bundle = Bundle().apply {
                    putSerializable(BUNDLE_KEY.USER_PROFILE, consultantResponse)
                    putInt(ROOT_SCREEN, rootScreen)
                }
                appNavigation.openLiveStreamToExitLiveStream(bundle)
            }.setOnNegativePressed {
                it.dismiss()
            }
        listAlertDialogShowing.add(dialog)
    }

    private fun dismissAllDialogShowing() {
        for (item in listAlertDialogShowing) {
            if (item.isShowing) {
                item.dismiss()
            }
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
                    val buyPointScreen = LiveStreamBuyPointFragment.newInstance(purchasePointUrl)
                    buyPointScreen.isCancelable = false
                    buyPointScreen.show(childFragmentManager, "LiveStreamBuyPointFragment")
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

    private fun dismissBottomSheet(tag: String) {
        val fragment: Fragment? = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null) (fragment as BottomSheetDialogFragment).dismiss()
    }

    override fun onClickButtonOKConfirmBottomSheet(mode: String) {
        when (mode) {
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                if (currentMode != LiveStreamMode.PREMIUM_PRIVATE) {
                    mViewModel.updateMode(PREMIUM_PRIVATE)
                    currentMode = LiveStreamMode.PREMIUM_PRIVATE
                    updateModeStatus()
                }
            }

            PRIVATE_MODE_REGISTER -> {
                if (currentMode != LiveStreamMode.PRIVATE) {
                    mViewModel.updateMode(PRIVATE)
                }
            }

            CHANGE_TO_PARTY_MODE, CHANGE_TO_PARTY_MODE_FROM_PEEP -> {
                if (currentMode != LiveStreamMode.PARTY) {
                    mViewModel.updateMode(PARTY)
                    mViewModel.setViewerStatus(0)
                }
            }

            PERFORMER_OUT_CONFIRM -> {}
        }
    }

    override fun onClickButtonCancelConnectPrivate() {
        // TODO Do nothing
    }

    private fun getInputComment() = binding.edtComment.text?.trim().toString()

    override fun onDestroyView() {
        requireContext().unregisterReceiver(earphoneEventReceiver)
        dismissAllDialogShowing()
        try {
            binding.performerView.release()
            binding.memberViewCamera.release()
        } catch (_: Exception) {

        } finally {
            mViewModel.logout()
            super.onDestroyView()
        }
    }

    override fun onSwitchViewerGroupVisible(isVisible: Boolean) {
        //Nothing
    }

    override fun getPresenterView(): VideoRendererView {
        return binding.performerView
    }

    override fun getViewerView(): VideoRendererView {
        return binding.memberViewCamera
    }
}

enum class TypeHeadPhone {
    Wired, Bluetooth, Speaker
}