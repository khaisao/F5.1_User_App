package jp.careapp.counseling.android.ui.verificationCodeHelp

import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.databinding.FragmentVerificationCodeHelpBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_LOGIN_WITH_EMAIL
import jp.careapp.counseling.android.ui.main.OnBackPressedListener
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_EDIT_EMAIL
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_REGISTER_WITH_EMAIL
import jp.careapp.counseling.android.utils.CONTACT_US_MAIL
import jp.careapp.counseling.android.utils.CONTACT_US_MODE
import jp.careapp.counseling.android.utils.ContactUsMode
import javax.inject.Inject

@AndroidEntryPoint
class VerificationCodeHelpFragment :
    BaseFragment<FragmentVerificationCodeHelpBinding, VerificationCodeHelpViewModel>(),
    OnBackPressedListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_verification_code_help

    private val viewModel: VerificationCodeHelpViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    private var email = ""
    private var codeScreen = SCREEN_LOGIN_WITH_EMAIL
    private var isFocusEditTextEmail = false
    private var isFocusEditTextVerify = false

    override fun getVM() = viewModel

    override fun initView() {
        super.initView()
        DeviceUtil.hideSoftKeyboard(activity)
        if (requireArguments() != null) {
            if (requireArguments().containsKey(BUNDLE_KEY.CODE_SCREEN)) {
                codeScreen = requireArguments()?.getInt(BUNDLE_KEY.CODE_SCREEN)!!
                email = requireArguments()?.getString(BUNDLE_KEY.EMAIL)!!
                isFocusEditTextEmail =
                    requireArguments()?.getBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_EMAIL)!!
                isFocusEditTextVerify =
                    requireArguments()?.getBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_VERIFY)!!
            }
        }
        binding.tvYourEmail.text = email
        binding.tvContentHelp.text = HtmlCompat.fromHtml(
            getString(R.string.description_information_help),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    shareViewModel.setFocusVerifyCode(isFocusEditTextVerify)
                    appNavigation.navigateUp()
                }
            }
        )

        binding.btnEditContact.setOnClickListener {
            if (!isDoubleClick) {
                when (codeScreen) {
                    SCREEN_LOGIN_WITH_EMAIL, SCREEN_REGISTER_WITH_EMAIL -> {
                        appNavigation.popopBackStackToDetination(R.id.inputEmailFragment)
                        shareViewModel.setFocusEditText(isFocusEditTextEmail)
                        shareViewModel.setFocusVerifyCode(isFocusEditTextVerify)
                    }
                    SCREEN_EDIT_EMAIL -> {
                        appNavigation.openEditMail()
                    }
                    else -> {}
                }
            }
        }

        binding.btReSendEmail.setOnClickListener { viewModel.resendOtp(email) }

        binding.btContactUs.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.openContactUs(
                    bundleOf(
                        CONTACT_US_MAIL to email,
                        CONTACT_US_MODE to ContactUsMode.CONTACT_WITH_MAIL
                    )
                )
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is VerifyCodeHelpActionState.ResendOtpSuccess -> appNavigation.navigateUp()
            }
        }
    }

    override fun onBackPressed() {
        shareViewModel.setFocusVerifyCode(isFocusEditTextVerify)
    }
}
