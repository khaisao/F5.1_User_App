package jp.slapp.android.android.ui.review_mode.user_detail_report

import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.ActionState
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.android.utils.replaceSystemWindowInsets
import jp.slapp.android.android.utils.showSoftKeyboard
import jp.slapp.android.databinding.FragmentRmUserDetailReportBinding
import javax.inject.Inject


@AndroidEntryPoint
class RMUserDetailReportFragment :
    BaseFragment<FragmentRmUserDetailReportBinding, RMUserDetailReportViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_user_detail_report

    private val mViewModel: RMUserDetailReportViewModel by viewModels()
    override fun getVM(): RMUserDetailReportViewModel = mViewModel

    override fun initView() {
        super.initView()

        binding.root.replaceSystemWindowInsets()

        setUpToolBar()

        binding.edtReasonReport.requestFocus()
        binding.edtReasonReport.postDelayed(
            { requireActivity().showSoftKeyboard(binding.edtReasonReport) },
            200
        )

        binding.edtReasonReport.addTextChangedListener {
            binding.toolBar.enableBtnRight(
                it.toString().trim().isNotEmpty()
            )
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
                if (!isDoubleClick) mViewModel.sendReportUser(getContent())
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.SendReportSuccess) {
                if (it.isSuccess) {
                    RMCommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                        .showDialog()
                        .setDialogTitle(resources.getString(R.string.thank_for_report))
                        .setTextPositiveButton(R.string.ok_en)
                        .setOnPositivePressed { dialog ->
                            dialog.dismiss()
                            binding.edtReasonReport.text?.clear()
                        }
                }
            }
        }
    }

    private fun getContent() = binding.edtReasonReport.text.toString().trim()

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}