package jp.slapp.android.android.ui.review_mode.enterName

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.databinding.FragmentRmEnterNameBinding
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

        binding.edtNickName.setTextChangeListener {
            binding.btnSubmit.isEnabled = binding.edtNickName.getText().isNotBlank()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

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
                RMCommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitle(R.string.your_name_not_enter)
                    .setTextPositiveSmallButton(R.string.ok)
                    .setOnPositiveSmallPressed {
                        it.dismiss()
                    }
            }
        }
    }
}