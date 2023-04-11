package jp.careapp.counseling.android.ui.review_mode.my_menu

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.CookieManager
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.MenuItem
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentRmMyMenuBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMMyMenuFragment : BaseFragment<FragmentRmMyMenuBinding, RMMyMenuViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val mViewModel: RMMyMenuViewModel by viewModels()
    override fun getVM() = mViewModel

    override val layoutId = R.layout.fragment_rm_my_menu

    private var mAdapter: RMMyMenuAdapter? = null

    override fun initView() {
        super.initView()

        mAdapter = RMMyMenuAdapter(requireContext()) {
            onClickItemMenu(it)
        }
        binding.rvMyMenu.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            mAdapter?.submitList(MenuItem.values().toList())
        }

        mViewModel.showData()
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.tvPoint.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.openRMTopToRMBuyPoint()
            }
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
                if (!isDoubleClick) appNavigation.openRMMyMenuToRMSettingContact()
            }
            MenuItem.TERMS_OF_SERVICE -> {
                if (!isDoubleClick) appNavigation.openRMTermOfService()
            }
            MenuItem.PRIVACY_POLICY -> {
                if (!isDoubleClick) appNavigation.openRMPrivacyPolicy()
            }
            else -> {}
        }
    }

    private fun showDialogWithdrawal() {
        context?.let { context ->
            RMCommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_withdrawal)
                .setTextPositiveButton(R.string.accept_withdrawal)
                .setTextNegativeButton(R.string.cancel)
                .setOnPositivePressed {
                    mViewModel.handleWithdrawal(getString(R.string.reason_withdrawal))
                    it.dismiss()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMMyMenuActionState.WithdrawalSuccess -> {
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
        mAdapter = null
        super.onDestroyView()
    }
}