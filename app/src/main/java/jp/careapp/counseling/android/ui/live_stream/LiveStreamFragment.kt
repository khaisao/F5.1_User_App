package jp.careapp.counseling.android.ui.live_stream

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil.Companion.hideKeyBoardWhenClickOutSide
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.getHeight
import jp.careapp.counseling.android.utils.showSoftKeyboard
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.camera_micrro_switch.CameraMicroSwitchBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheetDialogListener
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.ConnectPrivateBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.LiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.notice.LiveStreamNoticeBottomSheet
import jp.careapp.counseling.android.utils.PermissionUtils
import jp.careapp.counseling.android.utils.PermissionUtils.registerPermission
import jp.careapp.counseling.databinding.FragmentLiveStreamBinding
import javax.inject.Inject

@AndroidEntryPoint
class LiveStreamFragment : BaseFragment<FragmentLiveStreamBinding, LiveStreamViewModel>(),
    LiveStreamConfirmBottomSheetDialogListener, LiveStreamConnectPrivateListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_live_stream

    private val mViewModel: LiveStreamViewModel by viewModels()
    override fun getVM() = mViewModel

    private val cameraAndAudioPermissionLauncher =
        registerPermission { onCameraAndAudioPermissionResult(it) }

    private fun onCameraAndAudioPermissionResult(state: PermissionUtils.PermissionState) {
        when (state) {
            PermissionUtils.PermissionState.Denied -> {}
            PermissionUtils.PermissionState.Granted -> {

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
                isKeyboardShowing = false
                mViewModel.reloadMode()
                binding.llComment.isVisible = false
            }
        }
    }

    override fun onResume() {
        super.onResume()

//        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
    }

    override fun onStop() {
        super.onStop()

//        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun initView() {
        super.initView()

        binding.viewModel = mViewModel
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnComment.setOnClickListener {
            binding.groupAllBtn.isVisible = false
            binding.memberCommentViewGroup.isVisible = true
            binding.btnSendComment.getHeight { binding.edtComment.minimumHeight = it }
            binding.edtComment.requestFocus()
            requireContext().showSoftKeyboard(binding.edtComment)
            hideKeyBoardWhenClickOutSide(binding.root, binding.btnSendComment, requireActivity())
        }

        binding.btnWhisper.setOnClickListener {}

        binding.btnPrivate.setOnClickListener {
            if (!isDoubleClick) {
                showLiveStreamConfirmBottomSheet(PRIVATE_MODE_REGISTER, this)
            }
        }

        binding.btnParty.setOnClickListener { if (!isDoubleClick) showDialogChangeToPartyMode() }

        binding.btnVideoMic.setOnClickListener { if (!isDoubleClick) mViewModel.handleMicAndCamera() }

        binding.btnPoint.setOnClickListener { }

        binding.edtComment.addTextChangedListener {
            binding.btnSendComment.isEnabled = getInputComment().isNotBlank()
        }

        binding.btnSendComment.setOnClickListener {
            if (!isDoubleClick) {
                mViewModel.sendComment(getInputComment())
                binding.edtComment.text?.clear()
            }
        }

        binding.btnClose.setOnClickListener { if (!isDoubleClick) appNavigation.navigateUp() }

        binding.btnCameraFlip.setOnClickListener { }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.currentModeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                LiveStreamMode.PARTY -> {
                    binding.groupButtonPartyMode.isVisible = true
                }

                LiveStreamMode.PRIVATE -> {

                }

                LiveStreamMode.PREMIUM_PRIVATE -> {

                }

                LiveStreamMode.PEEP -> {

                }
            }
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is LiveStreamActionState.ChangeToPremiumPrivateFromPrivate -> {
                    showLiveStreamConfirmBottomSheet(PREMIUM_PRIVATE_MODE_REGISTER, this)
                }

                is LiveStreamActionState.OpenBottomSheetSettingCameraAndMic -> {
//                    cameraAndAudioPermissionLauncher.launchMultiplePermission(
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                            Manifest.permission.RECORD_AUDIO
//                        )
//                    )
                    val bottomSheet = CameraMicroSwitchBottomSheet()
                    bottomSheet.show(childFragmentManager, "")
                }
            }
        }
    }

    private fun showPrivateModeDenied() {
        val bottomSheet = LiveStreamNoticeBottomSheet()
        bottomSheet.show(childFragmentManager, "")
    }

    private fun showLiveStreamConfirmBottomSheet(
        mode: String,
        listener: LiveStreamConfirmBottomSheetDialogListener
    ) {
        val bottomSheet = LiveStreamConfirmBottomSheet.newInstance(mode, listener)
        bottomSheet.show(childFragmentManager, "")
    }

    private fun showDialogChangeToPartyMode() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitleWithString("プライベートモードが解除されました")
            .setTextOkButton(R.string.close)
            .setOnOkButtonPressed {
                mViewModel.changeMode(LiveStreamMode.PARTY)
            }
    }

    override fun onClickButtonOKConfirmBottomSheet(mode: String) {
        when (mode) {
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                mViewModel.changeMode(LiveStreamMode.PREMIUM_PRIVATE)
            }
            PRIVATE_MODE_REGISTER -> {
                mViewModel.changeMode(LiveStreamMode.PRIVATE)
                val bottomSheet = ConnectPrivateBottomSheet()
                bottomSheet.show(childFragmentManager, "")
            }
            PERFORMER_OUT_CONFIRM -> {}
            CHANGE_TO_PARTY_MODE -> {
                mViewModel.changeMode(LiveStreamMode.PARTY)
            }
        }
    }

    override fun onClickButtonCancelConnectPrivate() {
        mViewModel.changeMode(LiveStreamMode.PARTY)
    }

    private fun getInputComment() = binding.edtComment.text?.trim().toString()
}