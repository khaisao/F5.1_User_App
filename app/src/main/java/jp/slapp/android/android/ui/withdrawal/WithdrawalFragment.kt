package jp.slapp.android.android.ui.withdrawal

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.onTextChange
import jp.careapp.core.utils.setUnderlineAndClick
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentWithdrawalBinding
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalFragment : BaseFragment<FragmentWithdrawalBinding, WithdrawalViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_withdrawal

    private val mViewModel: WithdrawalViewModel by viewModels()
    override fun getVM(): WithdrawalViewModel = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.tvTitleWithdrawal.setUnderlineAndClick(
            R.string.finally_tks,
            R.color.color_B47AFF,
            71,
            74
        ) { appNavigation.openSettingNotification() }

        binding.edtReason.onTextChange {
            binding.btnConfirm.isEnabled = getInputReason().isNotBlank()
        }

        binding.btnConfirm.setOnClickListener { showDialogConfirmWithdrawal() }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is WithdrawalActionState.WithdrawalSuccess -> appNavigation.openWithdrawalFinish()
            }
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) findNavController().navigateUp()
            }
        })
    }

    private fun getInputReason() = binding.edtReason.text.trim().toString()

    private fun showDialogConfirmWithdrawal() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.title_confirm_withdraw)
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setOnPositivePressed {
                mViewModel.handleWithdrawal(getInputReason())
                it.dismiss()
            }
            .setTextNegativeButton(R.string.no_block_alert)
            .setOnNegativePressed {
                it.dismiss()
            }
    }
}
