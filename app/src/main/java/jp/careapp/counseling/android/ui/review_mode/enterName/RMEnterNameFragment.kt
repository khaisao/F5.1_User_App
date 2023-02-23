package jp.careapp.counseling.android.ui.review_mode.enterName

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentRmEnterNameBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMEnterNameFragment : BaseFragment<FragmentRmEnterNameBinding, RMEnterNameViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId: Int = R.layout.fragment_rm_enter_name

    private val mViewModel: RMEnterNameViewModel by viewModels()
    override fun getVM(): RMEnterNameViewModel = mViewModel

    override fun initView() {
        super.initView()

        binding.edtNickName.setHint(getString(R.string.nick_name))
        binding.edtNickName.setTextChangeListener { count ->
            binding.btnSubmit.isEnabled = count != 0
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMEnterNameActionState.SetNickNameSuccess -> {
                    if (it.isSuccess) {
                        appNavigation.openRMEnterNameToRmTop()
                    }
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnSubmit.setOnClickListener {
            if (binding.edtNickName.getText().isNotBlank()) {
                mViewModel.submitNickName(binding.edtNickName.getText())
            } else {
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitle(R.string.your_name_not_enter)
                    .setTextPositiveButton(R.string.ok)
                    .setOnPositivePressed {
                        it.dismiss()
                    }
            }
        }

        binding.tvTermsOfService.setOnClickListener {}

        binding.tvPrivacyPolicy.setOnClickListener {}
    }
}