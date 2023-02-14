package jp.careapp.counseling.android.ui.withdrawal

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.onTextChange
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.databinding.FragmentWithdrawalBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalFragment : BaseFragment<FragmentWithdrawalBinding, WithdrawalViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModels: WithdrawalViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_withdrawal
    override fun getVM(): WithdrawalViewModel = viewModels

    @Inject
    lateinit var appPreferences: RxPreferences
    override fun bindingStateView() {
        super.bindingStateView()
        binding.apply {
            appBar.apply {
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
                tvTitle.text = getString(R.string.unsubscribed)
                viewStatusBar.visibility = View.GONE
            }
            edtReasonForWithdrawal.onTextChange {
                if (edtReasonForWithdrawal.text.trim().isNullOrEmpty()) {
                    btnConfirm.background = resources.getDrawable(R.drawable.bg_text_40)
                    btnConfirm.isEnabled = false
                    btnConfirm.setTextColor(resources.getColor(R.color.color_6D5D9A))
                } else {
                    btnConfirm.background = resources.getDrawable(R.drawable.bg_gradient_common)
                    btnConfirm.isEnabled = true
                    btnConfirm.setTextColor(resources.getColor(R.color.white))
                }
            }
            btnConfirm.setOnClickListener {
                if (!isDoubleClick)
                    CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                        .showDialog()
                        .setDialogTitle(R.string.title_confirm_withdraw)
                        .setTextPositiveButton(R.string.confirm_block_alert)
                        .setOnPositivePressed {
                            viewModels.setParamsWithdrawal(edtReasonForWithdrawal.text.toString())
                            appPreferences.clear()
                            appNavigation.openWithdrawalFinish()
                            it.dismiss()
                        }
                        .setTextNegativeButton(R.string.no_block_alert)
                        .setOnNegativePressed {
                            it.dismiss()
                        }
            }
        }
        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
    }
}
