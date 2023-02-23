package jp.careapp.counseling.android.ui.review_mode.my_menu

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.webkit.CookieManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.BuildConfig
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.MenuItem
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentRmMyMenuBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMMyMenuFragment : BaseFragment<FragmentRmMyMenuBinding, RMMyMenuViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val shareViewModel: ShareViewModel by activityViewModels()
    private val viewModel: RMMyMenuViewModel by viewModels()
    override fun getVM() = viewModel

    override val layoutId = R.layout.fragment_rm_my_menu

    private val _adapter by lazy {
        RMMyMenuAdapter(requireContext()) {
            onClickItemMenu(it)
        }
    }

    override fun initView() {
        super.initView()

        binding.rvMyMenu.apply {
            setHasFixedSize(true)

            adapter = _adapter
            _adapter.submitList(MenuItem.values().toList())
        }
    }

    private fun onClickItemMenu(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.CHANGE_NICKNAME -> {
                if (!isDoubleClick) {
                    appNavigation.openRMTopToRMSettingNickName()
                }
            }
            MenuItem.PROFILE_MSG -> {
                if (!isDoubleClick) {
                    appNavigation.openRMTopToRMSettingProfileMessage()
                }
            }
            MenuItem.PUSH_NOTIFICATION -> {
                if (!isDoubleClick) {
                    appNavigation.openRMMyMenuToSettingPush()
                }
            }
            MenuItem.MICROPHONE_CAMERA_SETTINGS -> {
                if (!isDoubleClick) {
                    context?.let {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", it.packageName, null)
                        })
                    }
                }
            }
            MenuItem.BLOCK_LIST -> {
                if (!isDoubleClick) {
                    appNavigation.openRMTopToRMBLockList()
                }
            }
            MenuItem.WITHDRAWAL -> {
                if (!isDoubleClick) {
                    showDialogWithdrawal()
                }
            }
            MenuItem.DELETE_ACCOUNT -> {
                appNavigation.openRMMyMenuToRMSettingContact()
            }
            MenuItem.TERMS_OF_SERVICE -> {
                if (!isDoubleClick) {
                    openWebView(
                        "${BuildConfig.BROWSER_DOMAIN}${Define.TERM_PATH}",
                        R.string.term
                    )
                }
            }
            MenuItem.PRIVACY_POLICY -> {
                if (!isDoubleClick) {
                    openWebView(
                        "${BuildConfig.BROWSER_DOMAIN}${Define.PRIVACY_POLICY_PATH}",
                        R.string.privacy_policy
                    )
                }
            }
            MenuItem.COMMERCIAL_TRANSACTION_LAW -> {
                // TODO
            }
            else -> {}
        }
    }

    private fun showDialogWithdrawal() {
        context?.let { context ->
            CommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_withdrawal)
                .setTextPositiveButton(R.string.accept_withdrawal)
                .setTextNegativeButton(R.string.cancel)
                .setOnPositivePressed {
                    viewModel.handleWithdrawal(getString(R.string.reason_withdrawal))
                    it.dismiss()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }

    private fun openWebView(url: String, titleResId: Int) {
        Bundle().apply {
            putString(
                Define.TITLE_WEB_VIEW,
                getString(titleResId)
            )
            putString(
                Define.URL_WEB_VIEW,
                url
            )
        }.also {
            appNavigation.openScreenToWebview(it)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.WithdrawalSuccess) {
                if (it.isSuccess) {
                    reinitializeData()
                    appNavigation.openTabRMMyMenuToRMStart()
                }
            }
        }
    }

    private fun reinitializeData() {
        rxPreferences.clear()
        clearSession()
        removeAllOldPushNotification()
    }

    private fun clearSession() {
        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }
    }

    private fun removeAllOldPushNotification() {
        (context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
    }

    override fun onDestroyView() {
        binding.rvMyMenu.adapter = null
        super.onDestroyView()
    }
}