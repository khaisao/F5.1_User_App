package jp.careapp.counseling.android.ui.review_mode.userDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.RMUserDetailResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmUserDetailBinding
import jp.careapp.counseling.databinding.LlUserDetailBottomSheetBinding
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

            binding.tvNickName.text = it.name

            if (it.presenceStatus == 0) {
                binding.llItemOffline.root.visibility = View.VISIBLE
                binding.llItemOnline.root.visibility = View.INVISIBLE
            } else {
                binding.llItemOnline.root.visibility = View.VISIBLE
                binding.llItemOffline.root.visibility = View.INVISIBLE
            }
        }

        mViewModel.isFavorite.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivFavorite.loadImage(R.drawable.ic_add_favorite_active)
            } else {
                binding.ivFavorite.loadImage(R.drawable.ic_add_favorite_inactive)
            }
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
            CommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle("${user.name} さんををブロックしますか？")
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
            LlUserDetailBottomSheetBinding.inflate(LayoutInflater.from(requireContext()))

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