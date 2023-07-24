package jp.slapp.android.android.ui.review_mode.user_detail

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.data.model.live_stream.ConnectResult
import jp.slapp.android.android.model.network.RMUserDetailResponse
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.review_mode.calling.PerformerInfo
import jp.slapp.android.android.ui.review_mode.calling.RMCallConnectionDialog
import jp.slapp.android.android.ui.review_mode.calling.RMCallingViewModel
import jp.slapp.android.android.ui.review_mode.top.RMTopViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.SocketInfo
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.android.utils.performer_extension.PerformerStatusHandler
import jp.slapp.android.databinding.FragmentRmUserDetailBinding
import jp.slapp.android.databinding.LlRvUserDetailBottomSheetBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMUserDetailFragment : BaseFragment<FragmentRmUserDetailBinding, RMUserDetailViewModel>(),
    RMCallConnectionDialog.CallingCancelListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_user_detail

    private val viewModel: RMUserDetailViewModel by viewModels()
    private val callingViewModel: RMCallingViewModel by viewModels()
    override fun getVM(): RMUserDetailViewModel = viewModel

    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    private lateinit var user: RMUserDetailResponse

    private var viewerType = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val status =
                PerformerStatusHandler.getStatus(user.callStatus ?: 0, user.chatStatus ?: 0)
            callingViewModel.connectLiveStream(user.code ?: "", status)
        }
    }

    override fun initView() {
        super.initView()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setUpToolBar()

        binding.viewModel = viewModel

        viewModel.user.observe(viewLifecycleOwner) {
            user = it
            callingViewModel.userCode = it.code.toString()
            binding.ivProfile.loadImage(user.thumbnailImageUrl, R.drawable.ic_no_image)
            binding.tvNickName.text = it.name
            binding.tvNickName.bringToFront()
            binding.executePendingBindings()
        }

        Glide.with(binding.ivProfile).load(R.drawable.ic_no_image)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen._20sdp))
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivProfile)
        binding.executePendingBindings()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnMessageChat.setOnClickListener { viewModel.onClickMessageChat() }

        binding.btnCallVideo.setOnClickListener {
            if (!isDoubleClick) {
                val status =
                    PerformerStatusHandler.getStatus(user.callStatus ?: 0, user.chatStatus ?: 0)
                callingViewModel.connectLiveStream(user.code ?: "", status)
                viewerType = 0
                callingViewModel.viewerStatus = 0
            }
        }

        binding.ivFavorite.setOnClickListener {
            viewModel.onClickUserFavorite()
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }

            override fun onClickRight() {
                super.onClickRight()
                if (!isDoubleClick) showBlockBottomSheet()
            }
        })
    }

    private fun showDialogBlock(userName: String) {
        context?.let { context ->
            RMCommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(String.format(getString(R.string.dialog_msg_block), userName))
                .setTextPositiveButton(R.string.confirm_block_alert)
                .setTextNegativeButton(R.string.cancel)
                .setOnPositivePressed {
                    viewModel.blockUser()
                    it.dismiss()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }

    private fun showBlockBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bottomSheetBinding =
            LlRvUserDetailBottomSheetBinding.inflate(LayoutInflater.from(requireContext()))

        bottomSheetBinding.llItemBlockUser.setOnClickListener {
            viewModel.onClickBlock()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.llItemReportUser.setOnClickListener {
            viewModel.onClickReport()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMUserDetailActionState.NavigateToUserDetailMessage -> {
                    appNavigation.openRMUserDetailToUserDetailMsg(
                        bundleOf(
                            BUNDLE_KEY.PERFORMER_CODE to it.userCode,
                            BUNDLE_KEY.PERFORMER_NAME to it.userName,
                            BUNDLE_KEY.PERFORMER_IMAGE to it.thumbnailImage,
                        )
                    )
                }

                is RMUserDetailActionState.BlockUserSuccess -> {
                    rmTopViewModel.isNeedUpdateData = true
                    appNavigation.navigateUp()
                }

                is RMUserDetailActionState.AddAndDeleteFavoriteSuccess -> {
                    rmTopViewModel.isNeedUpdateData = true
                }
                is RMUserDetailActionState.ShowDialogBlock -> showDialogBlock(it.userName)

                is RMUserDetailActionState.NavigateToUserDetailReport -> {
                    appNavigation.openRMUserDetailToRMUserDetailReport(bundleOf(BUNDLE_KEY.PERFORMER_CODE to it.userCode))
                }
            }
        }
        callingViewModel.connectResult.observe(viewLifecycleOwner, connectResultHandle)
        callingViewModel.isLoginSuccess.observe(viewLifecycleOwner, loginSuccessHandle)

    }

    private val connectResultHandle: Observer<ConnectResult> = Observer {
        if (it.result != SocketInfo.RESULT_NONE) {
            user.let { performerResponse ->
                run {
                    val fragment: Fragment? =
                        childFragmentManager.findFragmentByTag("CallConnectionDialog")
                    val dialog: RMCallConnectionDialog
                    if (fragment != null) {
                        dialog = fragment as RMCallConnectionDialog
                        dialog.setCallingCancelListener(this@RMUserDetailFragment)
                        if (it.result == SocketInfo.RESULT_NG) {
                            dialog.setMessage(it.message, true)
                        } else {
                            dialog.setMessage(getString(R.string.call_content))
                        }
                    } else {
                        val message =
                            if (it.result == SocketInfo.RESULT_NG) it.message else getString(R.string.call_content)
                        val isError = it.result == SocketInfo.RESULT_NG
                        dialog =
                            RMCallConnectionDialog.newInstance(
                                PerformerInfo(
                                    name = user.name ?: "",
                                    performerCode = user.code ?: "",
                                    imageUrl = user.thumbnailImageUrl ?: ""
                                ),
                                message,
                                isError
                            )
                        dialog.setCallingCancelListener(this@RMUserDetailFragment)
                        dialog.show(childFragmentManager, "CallConnectionDialog")
                    }
                }
            }
        }
    }

    private val loginSuccessHandle: Observer<Boolean> = Observer {
        if (it) {
            val fragment: Fragment? = childFragmentManager.findFragmentByTag("CallConnectionDialog")
            if (fragment != null) (fragment as RMCallConnectionDialog).dismiss()
            val bundle = Bundle().apply {
                putSerializable(
                    BUNDLE_KEY.FLAX_LOGIN_AUTH_RESPONSE,
                    callingViewModel.flaxLoginAuthResponse
                )
                putSerializable(
                    BUNDLE_KEY.PERFORMER,
                    PerformerInfo(user.name ?: "", user.code ?: "", user.thumbnailImageUrl ?: "")
                )
                putInt(BUNDLE_KEY.VIEW_STATUS, viewerType)
            }
            appNavigation.openRMUserDetailToRMLivestream(bundle)
            callingViewModel.resetData()
        }
    }

    override fun callingCancel(isError: Boolean) {
        callingViewModel.cancelCall(isError)
        callingViewModel.resetData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.userProfileResult.removeObservers(viewLifecycleOwner)
        viewModel.statusFavorite.removeObservers(viewLifecycleOwner)
        viewModel.statusRemoveFavorite.removeObservers(viewLifecycleOwner)
        viewModel.isFirstChat.removeObservers(viewLifecycleOwner)
        viewModel.userGallery.removeObservers(viewLifecycleOwner)
        callingViewModel.connectResult.removeObservers(viewLifecycleOwner)
        viewModel.isButtonEnable.removeObservers(viewLifecycleOwner)
        callingViewModel.isLoginSuccess.removeObservers(viewLifecycleOwner)
    }
}