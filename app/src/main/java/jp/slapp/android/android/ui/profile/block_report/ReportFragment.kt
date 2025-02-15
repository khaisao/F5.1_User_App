package jp.slapp.android.android.ui.profile.block_report

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.databinding.FragmentUserReportBinding
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.utils.SignupStatusMode
import javax.inject.Inject

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentUserReportBinding, ReportViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_user_report

    private val viewModel: ReportViewModel by viewModels()

    override fun getVM(): ReportViewModel = viewModel

    @Inject
    lateinit var rxPreferences: RxPreferences

    // data screen
    private var performerCode: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            performerCode = bundle.getString(BUNDLE_KEY.USER_PROFILE).toString()
        }
    }

    private var isNavigateToEdit = false

    override fun initView() {
        super.initView()

        isNavigateToEdit = false

        if (rxPreferences.getSignupStatus() == SignupStatusMode.WITHOUT_VERIFY_EMAIL) {
            showNotVerifyWithEmailDialog()
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.contentReportEdt.addTextChangedListener {
            checkEnableButton()
        }
        binding.reportBtn.setOnClickListener {
            if (!isDoubleClick && !TextUtils.isEmpty(binding.contentReportEdt.text.toString())) {
                if (performerCode.isNotEmpty()) {
                    activity?.let { it1 ->
                        viewModel.sendReport(
                            performerCode,
                            binding.contentReportEdt.text.toString().trim(),
                            it1
                        )
                    }
                }
            }
        }
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    appNavigation.navigateUp()
                }
            }
        )
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.reportUserResult.observe(viewLifecycleOwner, handleReportResult)
    }

    private var handleReportResult: Observer<Boolean> = Observer {
        if (it) {
            CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                .showDialog()
                .setDialogTitleWithString(
                    resources.getString(R.string.thank_for_report)
                )
                .setTextOkButton(R.string.ok_en)
                .setOnOkButtonPressed { dialog ->
                    dialog.dismiss()
                    binding.contentReportEdt.text.clear()
                }
        }
    }

    private fun checkEnableButton() {
        binding.reportBtn.isEnabled =
            !TextUtils.isEmpty(binding.contentReportEdt.text.toString().trim())
    }

    private fun showNotVerifyWithEmailDialog() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.please_register_email_before_report)
            .setTextLargeOkButton(R.string.email_address_settings)
            .setOnOkLargeButtonPressed {
                appNavigation.openReportToEditProfile()
                isNavigateToEdit = true
                it.dismiss()
            }
            .setOnDismissListener {
                if(!isNavigateToEdit){
                    appNavigation.navigateUp()
                }
            }
    }
}
