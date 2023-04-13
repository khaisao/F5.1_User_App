package jp.careapp.counseling.android.ui.registration

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentRegistrationBinding
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding, RegistrationViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_registration

    private val mViewModel: RegistrationViewModel by viewModels()
    override fun getVM(): RegistrationViewModel = mViewModel

    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun initView() {
        super.initView()

        binding.edtNickName.addTextChangedListener {
            binding.btnRegistration.isEnabled = it.toString().trim().isNotEmpty()
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnRegistration.setOnClickListener {
            mViewModel.register(getContent(), binding.cbTerm.isChecked)
        }
    }

    private fun getContent() = binding.edtNickName.text.toString().trim()

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RegistrationActionState.SetNickNameSuccess -> {
                    appNavigation.openRegistrationToTopScreen()
                    shareViewModel.setHaveToken(true)
                }
            }
        }
    }
}
