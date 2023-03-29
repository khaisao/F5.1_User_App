package jp.careapp.counseling.android.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.databinding.FragmentOtherSettingBinding
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentOtherSettingBinding, SettingViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId: Int = R.layout.fragment_other_setting
    private val viewModels: SettingViewModel by viewModels()
    override fun getVM(): SettingViewModel = viewModels

    private var reviewManager: ReviewManager? = null

    override fun initView() {
        super.initView()
        viewModels.loadMemberInfo()
        reviewManager = activity?.let { ReviewManagerFactory.create(it) }
        val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        binding.tvVersion.text = getString(R.string.version, pInfo.versionName)
    }

    private fun showRateApp() {
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                val reviewInfo: ReviewInfo = task.getResult()
                val flow: Task<Void> =
                    activity?.let { reviewManager!!.launchReviewFlow(it, reviewInfo) } as Task<Void>
                flow.addOnCompleteListener({ task1 -> })
            } else {
                // TODO
                activity?.let {
                    viewModels.showAlertDialog(
                        it,
                        getString(R.string.in_app_review_error)
                    )
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        binding.apply {
            appBar.apply {
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_left))
                tvTitle.text = getString(R.string.setting)
                viewStatusBar.visibility = View.GONE
            }
            tvBlockList.apply {
                tvStart.text = getString(R.string.BlockList)
                item.setOnClickListener {
                    if (!isDoubleClick)
                        appNavigation.openSettingToBlockedList()
                }
            }
            tvWithdrawal.apply {
                tvStart.text = getString(R.string.withdrawal)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        if (viewModels.isEnableWithdrawal) {
                            if (viewModels.isOpenWithdrawal) appNavigation.openSettingToWithdrawal()
                            else appNavigation.openSettingToWithdrawalStart()
                        } else appNavigation.openSettingToWithdrawal()
                    }
                }
            }
            tvDeleteAccount.apply {
                tvStart.text = getString(R.string.delete_account)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        showConfirmDialogDeleteAccount()
                    }
                }
            }
            tvEvaluateKearia.apply {
                tvStart.text = getString(R.string.EvaluateKearia)
                divider.visibility = View.GONE
                item.setOnClickListener {
                    if (!isDoubleClick)
                        showRateApp()
                }
            }
            tvAboutCharges.apply {
                tvStart.text = getString(R.string.AboutCharges)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.AboutCharges))
                            putString(Define.URL_WEB_VIEW, Define.URL_ABOUT_CHARGES)
                        }
                        appNavigation.openSettingToWebview(bundle)
                    }
                }
            }
            tvHowToUse.apply {
                tvStart.text = getString(R.string.HowToUse)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.HowToUse))
                            putString(Define.URL_WEB_VIEW, Define.URL_HOW_TO_USE)
                        }
                        appNavigation.openSettingToWebview(bundle)
                    }
                }
            }
            tvTermsOfService.apply {
                tvStart.text = getString(R.string.TermsOfService)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.TermsOfService))
                            putString(Define.URL_WEB_VIEW, Define.URL_TERMS)
                        }
                        appNavigation.openSettingToWebview(bundle)
                    }
                }
            }
            tvPrivacyPolicy.apply {
                tvStart.text = getString(R.string.PrivacyPolicy)
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.PrivacyPolicy))
                            putString(Define.URL_WEB_VIEW, Define.URL_PRIVACY)
                        }
                        appNavigation.openSettingToWebview(bundle)
                    }
                }
            }
            tvDisplayBased.apply {
                tvStart.text = getString(R.string.DisplayBased)
                divider.visibility = View.GONE
                item.setOnClickListener {
                    if (!isDoubleClick) {
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.DisplayBased))
                            putString(Define.URL_WEB_VIEW, Define.URL_LEGAL)
                        }
                        appNavigation.openSettingToWebview(bundle)
                    }
                }
            }

            viewModels.memberInFoResult.observe(viewLifecycleOwner) {
                if (it?.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
                    if (viewModels.memberInFoResult.value?.sdkUser == 0 && viewModels.memberInFoResult.value?.point!! >= 30) {
                        viewModels.isEnableWithdrawal = true
                        viewModels.isOpenWithdrawal = false
                    } else {
                        viewModels.isEnableWithdrawal = false
                        viewModels.isOpenWithdrawal = true
                    }
                } else {
                    viewModels.isEnableWithdrawal = true
                    viewModels.isOpenWithdrawal = true
                }
            }
        }
    }

    private fun showConfirmDialogDeleteAccount() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_confirm_delete_account)
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setTextNegativeButton(R.string.no_block_alert)
            .setOnPositivePressed {
                appNavigation.openSettingToDeleteAccount()
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }
}
