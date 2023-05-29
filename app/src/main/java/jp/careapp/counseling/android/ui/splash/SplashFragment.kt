package jp.careapp.counseling.android.ui.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentSplashBinding
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_splash

    private val viewModel: SplashViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    override fun getVM() = viewModel

    @Inject
    lateinit var rxPreferences: RxPreferences

    private fun checkForUpdates() {
        val appUpdateManager = AppUpdateManagerFactory.create(requireContext())
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                viewModel.setUpdateable(true)
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setCancelableDialog(false)
                    .setDialogTitle(R.string.update_notie)
                    .setContent(R.string.update_notie_content)
                    .setTextPositiveButton(R.string.update_now)
                    .setOnPositivePressed {
                        try {
                            requireContext().startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "market://details?id=" +
                                                requireContext().packageName
                                    )
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            requireContext().startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "http://play.google.com/store/apps/details?id=" +
                                                requireContext().packageName
                                    )
                                )
                            )
                        }
                        it.dismiss()
                    }
            }
        }
    }

    override fun bindingAction() {
        super.bindingAction()

        viewModel.actionSPlash.observe(viewLifecycleOwner) {
            when (it) {
                SplashActionState.Finish -> {
                    viewModel.getAppMode()
                }
            }
        }

        viewModel.appMode.observe(viewLifecycleOwner) {
            viewModel.checkModeChange()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.screenCode.observe(viewLifecycleOwner) { screenCode ->
            if (viewModel.isUpdate.value != null) {
                if (!viewModel.isUpdate.value!!) {
                    when (screenCode) {
                        SplashViewModel.SCREEN_CODE_START -> appNavigation.openActionToLoginAndClearBackstack()
                        SplashViewModel.SCREEN_CODE_TOP -> {
                            appNavigation.openSplashToTopScreen()
                            shareViewModel.setHaveToken(true)
                        }
                        SplashViewModel.SCREEN_CODE_START_RM -> appNavigation.openSplashToRMStart()
                        SplashViewModel.SCREEN_CODE_REGISTER_RM -> appNavigation.openSplashToRMStart()
                        SplashViewModel.SCREEN_CODE_TOP_RM -> appNavigation.openSplashToRMTop()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForUpdates()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUpdateable(false)
    }
}
