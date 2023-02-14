package jp.careapp.counseling.android.ui.registration

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.registerName.RegisterNameFragment
import jp.careapp.counseling.android.ui.registerName.RegisterNameFragment.Companion.setOnFragmentCallBack
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.android.utils.customView.GenderSelectView
import jp.careapp.counseling.databinding.FragmentRegistrationBinding
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding, RegistrationViewModel>(),
    RegisterNameFragment.FragmentCallBacks {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_registration

    private val viewModel: RegistrationViewModel by viewModels()

    override fun getVM(): RegistrationViewModel = viewModel

    private val shareViewModel: ShareViewModel by activityViewModels()

    private var isLoginWithoutEmail = true

    override fun initView() {
        super.initView()
        setOnFragmentCallBack(this)
        binding.cbTerm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPrivacyTerm(if (isChecked) 1 else 0)
            updateStatusButtonRegister()
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.ivBack.apply {
            isVisible = isLoginWithoutEmail
            setOnClickListener {
                if (!isDoubleClick) {
                    appNavigation.navigateUp()
                }
            }
        }

        binding.viewEnterName.setOnClickListener {
            val nameBundle = Bundle().apply {
                putString(BUNDLE_KEY.NAME, getName())
            }
            appNavigation.openRegistrationToRegisterNameScreen(nameBundle)
        }

        binding.viewChooseGender.setOnClickListener {
            binding.genderSelectView.visibility = View.VISIBLE
            binding.genderSelectView.setOnChooseGender(
                object : GenderSelectView.ChooseGender {
                    override fun choose(pos: Int, title: String?) {
                        binding.genderSelectView.visibility = View.GONE
                        binding.tvGender.setText(title)
                        viewModel.setGender(pos)
                        updateStatusButtonRegister()
                    }
                }
            )
        }
        binding.viewDateOfBirth.setOnClickListener {
            context?.let { it1 -> viewModel.openDatePicker(getBirthDay(), it1) }
        }

        binding.btnRegistration.setOnClickListener {
            Bundle().also {
                it.putBoolean(BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL, !isLoginWithoutEmail)
                if (isLoginWithoutEmail) {
                    it.putSerializable(
                        BUNDLE_KEY.PARAM_REGISTRATION,
                        viewModel.getRegisterRequestWithoutMail()
                    )
                } else {
                    it.putSerializable(
                        BUNDLE_KEY.PARAM_REGISTRATION,
                        viewModel.getRegisterRequest()
                    )
                }
            }.let {
                appNavigation.openRegistrationToSelectCategoryScreen(it)
            }
        }

        binding.tvTermAndService.setOnClickListener {
            val webViewBundle = Bundle().apply {
                putString(Define.TITLE_WEB_VIEW, getString(R.string.title_terms))
                putString(Define.URL_WEB_VIEW, Define.URL_TERMS)
            }
            appNavigation.openRegistrationToTermsOfServiceScreen(webViewBundle)
        }
    }

    private fun getBirthDay(): String {
        return binding.tvBirthday.text.trim().toString()
    }

    private fun getName(): String {
        return binding.tvName.text.trim().toString()
    }

    private fun updateStatusButtonRegister() {
        if (TextUtils.isEmpty(binding.tvName.text.toString().trim()) ||
            TextUtils.isEmpty(binding.tvGender.text.toString().trim()) ||
            TextUtils.isEmpty(binding.tvBirthday.text.toString().trim()) ||
            viewModel.getPrivacyTerm() != 1
        ) {
            binding.btnRegistration.isEnabled = false
            return
        }
        binding.btnRegistration.isEnabled = true
    }

    private var dateOfBirthObserver: Observer<String> = Observer {
        binding.tvBirthday.setText(it)
        updateStatusButtonRegister()
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    private var openScreenObserver: Observer<Int> = Observer {
        when (it) {
            VerifyCodeViewModel.SCREEN_CODE_SELECT_CATEGORY -> {
                val bundle = Bundle()
                bundle.putBoolean(BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL, true)
                appNavigation.openRegistrationToSelectCategoryScreen(bundle)
                shareViewModel.setHaveToken(true)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.dateOfBirth.observeForever(dateOfBirthObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.codeScreenAfterRegistration.observeForever(openScreenObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLoginWithoutEmail =
            rxPreferences.getSignedUpStatus() == SignedUpStatus.LOGIN_WITHOUT_EMAIL
    }

    override fun onResume() {
        super.onResume()
        activity?.let { DeviceUtil.hideKeyBoard(it) }
        updateStatusButtonRegister()
        binding.tvName.setText(viewModel.getName())
    }

    override fun onCallBack(data: String?) {
        viewModel.setName(data.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.codeScreenAfterRegistration.removeObservers(viewLifecycleOwner)
        viewModel.isLoading.removeObservers(viewLifecycleOwner)
        viewModel.dateOfBirth.removeObservers(viewLifecycleOwner)
    }
}
