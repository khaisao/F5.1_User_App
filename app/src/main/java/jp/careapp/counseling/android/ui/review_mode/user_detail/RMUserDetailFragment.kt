package jp.careapp.counseling.android.ui.review_mode.user_detail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
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
import jp.careapp.counseling.android.model.network.RMUserDetailResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.ActionState
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

    private lateinit var user: RMUserDetailResponse

    override fun initView() {
        super.initView()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setUpToolBar()

        mViewModel.user.observe(viewLifecycleOwner) {
            user = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Glide.with(binding.ivProfile).load(
                    resources.getIdentifier(
                        "ic_no_image",
                        "drawable", requireContext().packageName
                    )
                )
                    .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen._20sdp)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivProfile)
            } else {
                Glide.with(binding.ivProfile).load(
                    resources.getIdentifier(
                        "ic_no_image",
                        "drawable", requireContext().packageName
                    )
                ).transforms(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen._20sdp))
                )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivProfile)
            }
            binding.tvNickName.text = it.name
            binding.tvNickName.bringToFront()
            binding.status = it.presenceStatus
            binding.follow = user.isFavorite
            binding.executePendingBindings()
        }

        mViewModel.isFavorite.observe(viewLifecycleOwner) {
            binding.follow = it
            binding.executePendingBindings()
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnMessageChat.setOnClickListener {
            Bundle().apply {
                putString(BUNDLE_KEY.PERFORMER_CODE, user.code ?: "")
                putString(BUNDLE_KEY.PERFORMER_NAME, user.name ?: "")
            }.let {
                appNavigation.openRMUserDetailToUserDetailMsg(it)
            }
        }

        binding.btnCallVideo.setOnClickListener { }

        binding.ivFavorite.setOnClickListener { mViewModel.onClickUserFavorite() }
    }

    private fun setUpToolBar() {
        binding.toolBar.setRootLayoutBackgroundColor(Color.TRANSPARENT)
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

    private fun showDialogBlock() {
        context?.let { context ->
            RMCommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(String.format(getString(R.string.dialog_msg_block), user.name))
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
            showDialogBlock()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.llItemReportUser.setOnClickListener {
            val bundle = Bundle().apply {
                putString(BUNDLE_KEY.PERFORMER_CODE, mViewModel.userCode)
            }
            appNavigation.openRMUserDetailToRMUserDetailReport(bundle)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.actionState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is ActionState.BlockUserSuccess -> {
                        if (it.isSuccess) {
                            rmTopViewModel.isNeedUpdateData = true
                            appNavigation.navigateUp()
                        }
                    }
                    is ActionState.AddAndDeleteFavoriteSuccess -> {
                        if (it.isSuccess) {
                            rmTopViewModel.isNeedUpdateData = true
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}