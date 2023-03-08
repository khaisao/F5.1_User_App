package jp.careapp.counseling.android.ui.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
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
import jp.careapp.counseling.android.utils.SignedUpStatus
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
        binding.tvName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setName(binding.tvName.text.toString().trim())
                updateStatusButtonRegister()
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.cbTerm.setOnClickListener {
            if (binding.cbTerm.isChecked) {
                viewModel.setReceiveMail(true)
            } else {
                viewModel.setReceiveMail(false)
            }
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
                    viewModel.register(viewModel.getRegisterRequest())
                }
            }
//                .let {
//                appNavigation.openRegistrationToSelectCategoryScreen(it)
//            }
        }
    }

    private fun getName(): String {
        return binding.tvName.text?.trim().toString()
    }

    private fun updateStatusButtonRegister() {
        if (TextUtils.isEmpty(binding.tvName.text.toString().trim())) {
            binding.btnRegistration.isEnabled = false
            return
        }
        binding.btnRegistration.isEnabled = true
    }

    private var dateOfBirthObserver: Observer<String> = Observer {
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
                appNavigation.openRegistrationToTopScreen(bundle)
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
