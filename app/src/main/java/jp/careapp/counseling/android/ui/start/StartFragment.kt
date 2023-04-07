package jp.careapp.counseling.android.ui.start

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel
import jp.careapp.counseling.android.ui.splash.SplashViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.Define.Companion.KEY_ALIAS
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.databinding.FragmentStartBinding
import me.leolin.shortcutbadger.ShortcutBadger
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : BaseFragment<FragmentStartBinding, StartViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var keyService: KeyService
    override val layoutId = R.layout.fragment_start
    private val viewModel: StartViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    override fun getVM(): StartViewModel = viewModel

    @Inject
    lateinit var rxPreferences: RxPreferences

    override fun initView() {
        super.initView()
        try {
            rxPreferences.saveNumberUnreadMessage(0)
            ShortcutBadger.removeCount(context)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        var webViewBundle: Bundle
        with(binding.rlSignInWithoutEmail) {
            setOnClickListener {
                if (!TextUtils.isEmpty(rxPreferences.getEmail()) && !TextUtils.isEmpty(
                        keyService.decrypt(
                            KEY_ALIAS,
                            rxPreferences.getPassword()
                        )
                    )
                ) {
                    viewModel.login(
                        rxPreferences.getEmail()!!,
                        keyService.decrypt(KEY_ALIAS, rxPreferences.getPassword())!!
                    )
                } else {
                    rxPreferences.setSignedUpStatus(SignedUpStatus.LOGIN_WITHOUT_EMAIL)
                    appNavigation.openStartWithoutLoginToRegistrationScreen()
                }
            }
        }
        with(binding.rlSignInWithEmail) {
            setOnClickListener {
                rxPreferences.setSignedUpStatus(SignedUpStatus.UNKNOWN)
                val bundle = Bundle().apply {
                    putInt(
                        BUNDLE_KEY.CODE_SCREEN,
                        InputAndEditMailViewModel.SCREEN_REGISTER_WITH_EMAIL
                    )
                }
                appNavigation.openStartToInputAndEditEmailScreen(bundle)
            }
        }
        with(binding.tvDifficult) {
            setOnClickListener {
                binding.tvDifficult.setTextColor(resources.getColor(R.color.color_6D5D9A))
                webViewBundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.title_difficult_with_login))
                    putString(Define.URL_WEB_VIEW, Define.URL_TROUBLE_LOGIN)
                }
                appNavigation.openStartToDifficultLoginScreen(webViewBundle)
            }
        }

        with(binding.tvTermService) {
            setOnClickListener {
                binding.tvTermService.setTextColor(resources.getColor(R.color.color_6D5D9A))
                webViewBundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.title_terms))
                    putString(Define.URL_WEB_VIEW, Define.URL_TERMS)
                }
                appNavigation.openStartToTermsScreen(webViewBundle)
            }
        }

        with(binding.tvPrivacy) {
            setOnClickListener {
                binding.tvPrivacy.setTextColor(resources.getColor(R.color.color_6D5D9A))
                webViewBundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.title_privacy))
                    putString(Define.URL_WEB_VIEW, Define.URL_PRIVACY)
                }
                appNavigation.openStartToPrivacyScreen(webViewBundle)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }

        viewModel.screenCode.observe(viewLifecycleOwner) {
            when (it) {
                SplashViewModel.SCREEN_CODE_START -> {
                    rxPreferences.setSignedUpStatus(SignedUpStatus.LOGIN_WITHOUT_EMAIL)
                    appNavigation.openStartWithoutLoginToRegistrationScreen()
                    viewModel.screenCode.value = -1
                }
                SplashViewModel.SCREEN_CODE_TOP -> {
                    appNavigation.openStartToTopScreen()
                    shareViewModel.setHaveToken(true)
                }
            }
        }
    }
}
