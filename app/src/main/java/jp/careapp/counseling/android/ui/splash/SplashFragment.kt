package jp.careapp.counseling.android.ui.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
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
                    viewModel.handleActionSplash()
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }

        viewModel.screenCode.observe(viewLifecycleOwner) { screenCode ->
            viewModel.isUpdate.observe(viewLifecycleOwner, Observer {
                if (!it)
                    when (screenCode) {
                        SplashViewModel.SCREEN_CODE_START -> appNavigation.openSplashToStartScreen()
                        SplashViewModel.SCREEN_CODE_TOP -> {
                            appNavigation.openSplashToTopScreen()
                            shareViewModel.setHaveToken(true)
                        }
                        SplashViewModel.SCREEN_CODE_REREGISTER -> appNavigation.openSplashToReRegisterScreen()
                        SplashViewModel.SCREEN_CODE_BAD_USER -> appNavigation.openSplashToBadUserScreen()
                    }
            })
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