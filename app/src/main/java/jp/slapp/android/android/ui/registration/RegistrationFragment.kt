package jp.slapp.android.android.ui.registration

import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.data.network.InfoUserResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentRegistrationBinding
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding, RegistrationViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_registration

    private val mViewModel: RegistrationViewModel by viewModels()
    override fun getVM(): RegistrationViewModel = mViewModel

    private val shareViewModel: ShareViewModel by activityViewModels()

    var emailRegister = ""
    lateinit var dataResponseRegister : InfoUserResponse

    override fun initView() {
        super.initView()
        try {
            if (arguments != null) {
                dataResponseRegister =
                    arguments?.getSerializable(BUNDLE_KEY.DATA_RESPONSE_REGISTER) as InfoUserResponse
                emailRegister = arguments?.getString(BUNDLE_KEY.EMAIL_REGISTER).toString()
            }
        } catch (_: Exception) {
        }

        binding.edtNickName.addTextChangedListener {
            binding.btnRegistration.isEnabled = it.toString().trim().isNotEmpty()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {}
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnRegistration.setOnClickListener {
            if (this::dataResponseRegister.isInitialized && dataResponseRegister.token != null) {
                mViewModel.register(
                    getContent(), binding.cbTerm.isChecked,
                    dataResponseRegister.token!!, emailRegister
                )
            }
        }
    }

    private fun getContent() = binding.edtNickName.text.toString().trim()

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RegistrationActionState.NavigateToTop -> {
                    appNavigation.openRegistrationToTopScreen()
                    shareViewModel.setHaveToken(true)
                }
                is RegistrationActionState.NavigateToTopRM -> {
                    appNavigation.openRegistrationToRMTop()
                    shareViewModel.setHaveToken(true)
                }
            }
        }
    }

}
