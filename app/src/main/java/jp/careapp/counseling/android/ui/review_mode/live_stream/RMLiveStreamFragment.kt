package jp.careapp.counseling.android.ui.review_mode.live_stream

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil.Companion.getScreenHeightWithNavigationBar
import jp.careapp.core.utils.DeviceUtil.Companion.hideKeyBoardWhenClickOutSide
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.careapp.core.utils.getHeight
import jp.careapp.core.utils.setMarginsInDp
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FlaxLoginAuthResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.network.socket.MaruCastManager
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamMode
import jp.careapp.counseling.android.ui.live_stream.LiveStreamViewModel
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.LiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch.RMCameraMicroSwitchBottomSheet
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch.RMLiveStreamConfirmBottomSheet
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch.RMLiveStreamConfirmBottomSheetDialogListener
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch.RMLiveStreamMicAndCameraChangeCallback
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.notice.RMLiveStreamNoticeBottomSheet
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.rm_connect_private.RMConnectPrivateBottomSheet
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.rm_connect_private.RMLiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_dialog.RMLogoutConfirmDialog
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.PermissionUtils
import jp.careapp.counseling.android.utils.PermissionUtils.launchMultiplePermission
import jp.careapp.counseling.android.utils.PermissionUtils.registerPermission
import jp.careapp.counseling.android.utils.SocketInfo
import jp.careapp.counseling.android.utils.showSoftKeyboard
import jp.careapp.counseling.databinding.FragmentRmLiveStreamBinding
import org.marge.marucast_android_client.views.VideoRendererView
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RMLiveStreamFragment : BaseFragment<FragmentRmLiveStreamBinding, RMLiveStreamViewModel>(),
    RMLiveStreamConfirmBottomSheetDialogListener, LiveStreamConnectPrivateListener,
    MaruCastManager.SwitchViewerCallback, RMLiveStreamMicAndCameraChangeCallback {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_live_stream

    private val mViewModel: RMLiveStreamViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: RMLiveStreamAdapter? = null

    private var currentMode = LiveStreamMode.PARTY

    private var filter: IntentFilter? = null

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
            }
            PermissionUtils.PermissionState.Granted -> {
                RMCameraMicroSwitchBottomSheet.newInstance(
                    mViewModel.isMicMute(),
                    mViewModel.isCameraMute(),
                    this
                ).show(childFragmentManager, "CameraMicroSwitchBottomSheet")
                if (!mViewModel.isMicMute() && !mViewModel.isCameraMute()) {
                    maruCastManager.publishStream()
                    binding.clMemberCamera.visibility = View.VISIBLE
                }
            }
            PermissionUtils.PermissionState.PermanentlyDenied -> {
            }
        }
    }

    @Inject
    lateinit var maruCastManager: MaruCastManager

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
                        setMarginsInDp(
                            8,
                            0,
                            0,
                            8
                        )
                    }
                }
            } else {
                if (isKeyboardShowing) {
                    binding.rcvCommentList.apply {
                        updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomToTop = binding.barrier2.id
                        }
                        setMarginsInDp(
                            8,
                            0,
                            0,
                            20
                        )
                    }
                    binding.memberCommentViewGroup.isVisible = false
                    updateModeStatus()
                    isKeyboardShowing = false
                }
            }
        } catch (_: Exception) {
        }
    }

    private var xDown = 0f
    private var yDown = 0f
    private var initCameraX = 0f
    private var initCameraY = 0f

    private val cameraChangeVisibilityListener = ViewTreeObserver.OnGlobalLayoutListener {
        val newVis: Int = binding.clMemberCamera.visibility
        if (newVis == View.VISIBLE) {
            initCameraX = binding.clMemberCamera.x
            initCameraY = binding.clMemberCamera.y
            removeVisibilityChangeListener()
        }
    }

    private val listAlertDialogShowing = arrayListOf<Dialog>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPress()
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

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        arguments?.let {
            mViewModel.setFlaxLoginAuthResponse(it.getSerializable(BUNDLE_KEY.FLAX_LOGIN_AUTH_RESPONSE) as FlaxLoginAuthResponse)
            val viewStatus = it.getInt(BUNDLE_KEY.VIEW_STATUS)
            currentMode = if (viewStatus == 1) LiveStreamMode.PEEP else LiveStreamMode.PARTY
            mViewModel.setViewerStatus(viewStatus)
        }

        binding.viewModel = mViewModel

        binding.performerView.layoutParams.height =
            getScreenHeightWithNavigationBar(requireActivity())

        mViewModel.handleConnect(requireActivity(), this)

        updateModeStatus()

        filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
        requireContext().registerReceiver(earphoneEventReceiver, filter)

        mAdapter = RMLiveStreamAdapter()
        binding.rcvCommentList.adapter = mAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClick() {
        super.setOnClick()

        binding.btnComment.setOnClickListener {
            if (!isDoubleClick) {
                binding.groupAllBtn.visibility = View.INVISIBLE
                binding.memberCommentViewGroup.isVisible = true
                binding.edtComment.requestFocus()
                requireContext().showSoftKeyboard(binding.edtComment)
                binding.btnSendComment.getHeight { binding.edtComment.minimumHeight = it }
                binding.edtComment.text?.clear()

                /** Handle camera view to initialize position*/
                binding.clMemberCamera.x = initCameraX
                binding.clMemberCamera.y = initCameraY
            }
        }

        hideKeyBoardWhenClickOutSide(
            binding.root,
            arrayListOf(binding.btnSendComment, binding.rcvCommentList),
            requireActivity()
        )

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
                if (currentMode == LiveStreamMode.PREMIUM_PRIVATE) {
                    cameraAndAudioPermissionLauncher.launchMultiplePermission(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                }
            }
        }

        binding.edtComment.addTextChangedListener {
            binding.btnSendComment.isEnabled = getInputComment().isNotBlank()
        }

        binding.btnSendComment.setOnClickListener {
            if (!isDoubleClick) {
                mViewModel.sendComment(getInputComment())
                binding.edtComment.text?.clear()
            }
        }

        binding.btnEndCall.setOnClickListener { if (!isDoubleClick) showLogoutConfirm() }

        binding.btnCameraFlip.setOnClickListener {
            maruCastManager.switchCamera()
        }

        binding.clMemberCamera.setOnTouchListener { v, event ->
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
                        binding.clMemberCamera.x = binding.clMemberCamera.x + (newCameraX - it.x)
                        binding.clMemberCamera.y = binding.clMemberCamera.y + (newCameraY - it.y)
                        it.x = newCameraX
                        it.y = newCameraY
                    }
                }
            }
            true
        }
    }

    private fun showLogoutConfirm() {
        val dialog = RMLogoutConfirmDialog(requireContext())
            .showDialog()
            .setOnPositivePressed {
                it.dismiss()
                logout()
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

    private fun removeVisibilityChangeListener() {
        binding.clMemberCamera.viewTreeObserver.removeOnGlobalLayoutListener(
            cameraChangeVisibilityListener
        )
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

    override fun bindingStateView() {
        super.bindingStateView()
        bindingConnectResult()
        bindingUpdateUIMode()
        bindingViewerType()
        bindingTwoShotHandle()
        bindingMessageHandle()
    }

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


    private fun bindingConnectResult() {
        mViewModel.connectResult.observe(viewLifecycleOwner) {
            if (it.result == SocketInfo.RESULT_NG) {
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
                LiveStreamViewModel.UI_DISMISS_PRIVATE_MODE -> {
                    dismissBottomSheet("ConnectPrivateBottomSheet")
                }
                LiveStreamViewModel.UI_SHOW_CONFIRM_CLOSE_PRIVATE_MODE -> {
                    dismissBottomSheet("ConnectPrivateBottomSheet")
                    showPrivateModeDenied()
                }
                LiveStreamViewModel.UI_SHOW_WAITING_PRIVATE_MODE -> {
                    showPrivateModeRequest()
                }
                LiveStreamViewModel.UI_BUY_POINT -> {

                }
            }
        }
    }

    private fun bindingTwoShotHandle() {
        mViewModel.twoShot.observe(viewLifecycleOwner) {
            when {
                mViewModel.viewerStatus.value == 1 -> currentMode = LiveStreamMode.PEEP
                it == LiveStreamViewModel.TWO_SHOT_VALUE_0 -> currentMode = LiveStreamMode.PARTY
                it == LiveStreamViewModel.TWO_SHOT_VALUE_2 -> currentMode =
                    LiveStreamMode.PREMIUM_PRIVATE
            }
            updateModeStatus()
        }
    }

    private fun bindingViewerType() {
        mViewModel.viewerStatus.observe(viewLifecycleOwner) {
            when {
                mViewModel.twoShot.value == LiveStreamViewModel.TWO_SHOT_VALUE_0 -> currentMode =
                    LiveStreamMode.PARTY
                mViewModel.twoShot.value == LiveStreamViewModel.TWO_SHOT_VALUE_2 -> currentMode =
                    LiveStreamMode.PREMIUM_PRIVATE
            }
        }
    }

    private fun bindingMessageHandle() {
        mViewModel.messageList.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                binding.rcvCommentList.visibility=View.GONE
            }
            else{
                binding.rcvCommentList.visibility=View.VISIBLE
                mAdapter?.submitList(it)
                binding.rcvCommentList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
            }

        }
    }

    private fun updateModeStatus() {
        when (currentMode) {
            LiveStreamMode.PARTY -> {
                binding.groupAllBtn.visibility = View.GONE
                binding.groupButtonPartyMode.visibility = View.VISIBLE
            }
            LiveStreamMode.PREMIUM_PRIVATE -> {
                binding.groupAllBtn.visibility = View.GONE
                binding.groupButtonPrivateMode.visibility = View.VISIBLE
            }
        }
    }

    private fun logout() {
        appNavigation.navigateUp()
        mViewModel.logout()
    }

    private fun showPrivateModeDenied() {
        val bottomSheet = RMLiveStreamNoticeBottomSheet()
        bottomSheet.show(childFragmentManager, "LiveStreamNoticeBottomSheet")
    }

    private fun showLiveStreamConfirmBottomSheet(
        mode: String,
        listener: RMLiveStreamConfirmBottomSheetDialogListener
    ) {
        val bottomSheet = RMLiveStreamConfirmBottomSheet.newInstance(mode, listener)
        bottomSheet.show(childFragmentManager, "")
    }

    override fun onClickButtonOKConfirmBottomSheet(mode: String) {
        when (mode) {
            PRIVATE_MODE_REGISTER -> {
                mViewModel.updateMode(LiveStreamViewModel.PREMIUM_PRIVATE)
            }
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                mViewModel.updateMode(LiveStreamViewModel.PREMIUM_PRIVATE)
                currentMode = LiveStreamMode.PREMIUM_PRIVATE
                updateModeStatus()
            }
            CHANGE_TO_PARTY_MODE -> {
                mViewModel.updateMode(LiveStreamViewModel.PARTY)
            }
        }
    }

    override fun onClickButtonCancelConnectPrivate() {
        currentMode = LiveStreamMode.PREMIUM_PRIVATE
    }

    private fun showErrorDialog(errorMessage: String) {
        val dialog = RMCommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(errorMessage)
            .setTextPositiveButton(R.string.ok)
            .setOnPositivePressed {
                if (errorMessage == resources.getString(R.string.already_log_out)) {
                    logout()
                } else {
                    it.dismiss()
                }
            }
        listAlertDialogShowing.add(dialog)
    }

    private fun showPrivateModeRequest() {
        RMConnectPrivateBottomSheet.newInstance(object : RMLiveStreamConnectPrivateListener {
            override fun onClickButtonCancelConnectPrivate() {
                mViewModel.privateModeCancel()
            }
        }).show(childFragmentManager, "ConnectPrivateBottomSheet")
    }

    private fun dismissBottomSheet(tag: String) {
        val fragment: Fragment? = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null) (fragment as BottomSheetDialogFragment).dismiss()
    }

    private fun getInputComment() = binding.edtComment.text?.trim().toString()

    override fun onSwitchViewerGroupVisible(isVisible: Boolean) {
        binding.memberCameraViewGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.clMemberCamera.viewTreeObserver.addOnGlobalLayoutListener(
            cameraChangeVisibilityListener
        )
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
        binding.memberCameraViewGroup.visibility = if (_isCameraMute) View.GONE else View.VISIBLE
    }
}