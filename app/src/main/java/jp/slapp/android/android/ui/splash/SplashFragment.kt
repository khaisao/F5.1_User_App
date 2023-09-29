package jp.slapp.android.android.ui.splash

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.BuildConfig
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.databinding.FragmentSplashBinding
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

    companion object {
        private const val DIRECTORY_APK_URL = BuildConfig.WEB_DOMAIN + "/app/supakura.apk"
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

        viewModel.actionUpdate.observe(viewLifecycleOwner) { isNeedUpdate ->
            if (isNeedUpdate) {
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setCancelableDialog(false)
                    .setDialogTitle(R.string.update_notie)
                    .setContent(R.string.update_notie_content)
                    .setTextOkButton(R.string.update_now)
                    .setOnOkButtonPressed {
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(DIRECTORY_APK_URL)
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
        }
    }

}
