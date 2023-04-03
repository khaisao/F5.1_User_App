package jp.careapp.counseling.android.ui.delete_account

import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.onTextChange
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.result.Result
import jp.careapp.counseling.databinding.FragmentDeleteAccountBinding
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding, DeleteAccountViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_delete_account
    private val viewModel: DeleteAccountViewModel by viewModels()
    override fun getVM(): DeleteAccountViewModel = viewModel

    override fun initView() {
        super.initView()
        binding.apply {
            appBar.apply {
                btnLeft.setImageDrawable(context?.let { getDrawable(it, R.drawable.ic_arrow_left) })
                btnLeft.setOnClickListener {
                    if (!isDoubleClick) {
                        appNavigation.navigateUp()
                    }
                }
                tvTitle.text = getString(R.string.delete_account)
                viewStatusBar.visibility = View.GONE
            }
            edtReasonForDelete.onTextChange {
                context?.let {
                    if (edtReasonForDelete.text.trim().isNullOrEmpty()) {
                        btnConfirm.background = getDrawable(it, R.drawable.bg_text_40)
                        btnConfirm.setTextColor(getColor(it, R.color.color_6D5D9A))
                        btnConfirm.isEnabled = false
                    } else {
                        btnConfirm.background = getDrawable(it, R.drawable.bg_gradient_common)
                        btnConfirm.setTextColor(getColor(it, R.color.white))
                        btnConfirm.isEnabled = true
                    }
                }

            }
            btnConfirm.setOnClickListener {
                if (!isDoubleClick) {
                    viewModel.deleteAccount(edtReasonForDelete.text.toString().trim())
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.deleteAccount.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    activity?.let { activity ->
                        CommonAlertDialog.getInstanceCommonAlertdialog(activity)
                            .showDialog()
                            .setDialogTitleWithString(getString(R.string.delete_my_account))
                            .setTextPositiveButton(jp.careapp.core.R.string.text_OK)
                            .setOnPositivePressed {
                                viewModel.clearLocalData()
                                appNavigation.openActionToLogin()
                                it.dismiss()
                            }
                    }
                }
                is Result.Error -> {
                    activity?.let { viewModel.showError(result.throwable, it) }
                }
                else -> {}
            }
        }
    }

}