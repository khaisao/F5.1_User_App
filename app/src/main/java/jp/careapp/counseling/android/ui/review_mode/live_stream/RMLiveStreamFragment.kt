package jp.careapp.counseling.android.ui.review_mode.live_stream

import android.Manifest
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil.Companion.getScreenHeightWithNavigationBar
import jp.careapp.core.utils.DeviceUtil.Companion.hideKeyBoardWhenClickOutSide
import jp.careapp.core.utils.getHeight
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.live_stream.*
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmBottomSheetDialogListener
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.ConnectPrivateBottomSheet
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.connect_private.LiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.review_mode.live_stream.live_stream_bottom_sheet.camera_micrro_switch.RMCameraMicroSwitchBottomSheet
import jp.careapp.counseling.android.utils.PermissionUtils
import jp.careapp.counseling.android.utils.PermissionUtils.launchMultiplePermission
import jp.careapp.counseling.android.utils.PermissionUtils.registerPermission
import jp.careapp.counseling.android.utils.showSoftKeyboard
import jp.careapp.counseling.databinding.FragmentRmLiveStreamBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMLiveStreamFragment : BaseFragment<FragmentRmLiveStreamBinding, RMLiveStreamViewModel>(),
    LiveStreamConfirmBottomSheetDialogListener, LiveStreamConnectPrivateListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_live_stream

    private val mViewModel: RMLiveStreamViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: LiveStreamAdapter? = null

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
                binding.memberCommentViewGroup.isVisible = false
                mViewModel.reloadMode()
                isKeyboardShowing = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.viewModel = mViewModel

        binding.performerView.layoutParams.height = getScreenHeightWithNavigationBar(requireActivity())

        mAdapter = LiveStreamAdapter()
        binding.rcvCommentList.adapter = mAdapter

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnComment.setOnClickListener {
            if (!isDoubleClick) {
                binding.groupAllBtn.visibility = View.INVISIBLE
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
//        hideKeyBoardWhenClickOutSide(binding.root, binding.rcvCommentList, requireActivity())


        binding.btnPrivate.setOnClickListener {
            if (!isDoubleClick) mViewModel.changeMode(LiveStreamMode.PRIVATE)
        }

        binding.btnParty.setOnClickListener {
            if (!isDoubleClick) mViewModel.changeMode(LiveStreamMode.PARTY)
        }

        binding.btnVideoMic.setOnClickListener { if (!isDoubleClick) mViewModel.handleMicAndCamera() }


        binding.edtComment.addTextChangedListener {
            binding.btnSendComment.isEnabled = getInputComment().isNotBlank()
        }

        binding.btnSendComment.setOnClickListener {
            if (!isDoubleClick) {
                mViewModel.sendComment(getInputComment())
                binding.edtComment.text?.clear()
            }
        }

        binding.btnEndCall.setOnClickListener { if (!isDoubleClick) appNavigation.navigateUp() }

        binding.btnCameraFlip.setOnClickListener { }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.currentModeLiveData.observe(viewLifecycleOwner) {
            binding.groupAllBtn.isVisible = false
            when (it) {
                LiveStreamMode.PARTY -> {
                    binding.groupButtonPartyMode.isVisible = true
                }

                LiveStreamMode.PRIVATE -> {
                    binding.groupButtonPrivateMode.isVisible = true
                }

            }
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is LiveStreamActionState.ChangeToPremiumPrivateFromPrivate -> {
                    showLiveStreamConfirmBottomSheet(PREMIUM_PRIVATE_MODE_REGISTER, this)
                }

                is LiveStreamActionState.OpenBottomSheetSettingCameraAndMic -> {
                    cameraAndAudioPermissionLauncher.launchMultiplePermission(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                    val bottomSheet = RMCameraMicroSwitchBottomSheet()
                    bottomSheet.show(childFragmentManager, "")
                }
            }
        }

        mViewModel.commentList.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }
    }

    private fun showLiveStreamConfirmBottomSheet(
        mode: String,
        listener: LiveStreamConfirmBottomSheetDialogListener
    ) {
        val bottomSheet = LiveStreamConfirmBottomSheet.newInstance(mode, listener)
        bottomSheet.show(childFragmentManager, "")
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}