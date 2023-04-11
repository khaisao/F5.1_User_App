package jp.careapp.counseling.android.ui.review_mode.user_detail

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmUserDetailBinding
import jp.careapp.counseling.databinding.LlRvUserDetailBottomSheetBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMUserDetailFragment : BaseFragment<FragmentRmUserDetailBinding, RMUserDetailViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_user_detail

    private val mViewModel: RMUserDetailViewModel by viewModels()
    override fun getVM(): RMUserDetailViewModel = mViewModel

    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.viewModel = mViewModel

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

        binding.btnMessageChat.setOnClickListener { mViewModel.onClickMessageChat() }

        binding.btnCallVideo.setOnClickListener { mViewModel.onClickCallVideo() }

        binding.ivFavorite.setOnClickListener { mViewModel.onClickUserFavorite() }
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
                    mViewModel.blockUser()
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
            mViewModel.onClickBlock()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.llItemReportUser.setOnClickListener {
            mViewModel.onClickReport()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMUserDetailActionState.NavigateToUserDetailMessage -> {
                    appNavigation.openRMUserDetailToUserDetailMsg(
                        bundleOf(
                            BUNDLE_KEY.PERFORMER_CODE to it.userCode,
                            BUNDLE_KEY.PERFORMER_NAME to it.userName
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
    }
}