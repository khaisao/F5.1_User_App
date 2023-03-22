package jp.careapp.counseling.android.ui.live_stream

import android.Manifest
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.camera_micrro_switch.CameraMicroSwitchBottomSheet
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.confirm.LiveStreamConfirmBottomSheet
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.confirm.LiveStreamConfirmBottomSheetDialogListener
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.connect_private.ConnectPrivateBottomSheet
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.connect_private.LiveStreamConnectPrivateListener
import jp.careapp.counseling.android.ui.live_stream.bottom_sheet_live_stream.notice.LiveStreamNoticeBottomSheet
import jp.careapp.counseling.android.utils.PermissionUtils
import jp.careapp.counseling.android.utils.PermissionUtils.launchMultiplePermission
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

    override fun setOnClick() {
        super.setOnClick()

        binding.btnComment.setOnClickListener {}

        binding.btnWhisper.setOnClickListener {}

        binding.btnPrivate.setOnClickListener {
            showLiveStreamConfirmBottomSheet(
                PRIVATE_MODE_REGISTER,
                this
            )
        }

        binding.btnParty.setOnClickListener { showDialogChangeToPartyMode() }

        binding.btnVideoMic.setOnClickListener { mViewModel.handleMicAndCamera() }

        binding.btnPoint.setOnClickListener { }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.currentMode.observe(viewLifecycleOwner) {

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
                mViewModel.changeToPartyMode()
            }
    }

    override fun onClickButtonOKConfirmBottomSheet(mode: String) {
        when (mode) {
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                mViewModel.changeToPremiumPrivateMode()
            }
            PRIVATE_MODE_REGISTER -> {
                mViewModel.changeToPrivateMode()
                val bottomSheet = ConnectPrivateBottomSheet()
                bottomSheet.show(childFragmentManager, "")
            }
            PERFORMER_OUT_CONFIRM -> {}
            CHANGE_TO_PARTY_MODE -> {
                mViewModel.changeToPartyMode()
            }
        }
    }

    override fun onClickButtonCancelConnectPrivate() {
        mViewModel.changeToPartyMode()
    }
}