package jp.slapp.android.android.ui.edit_mail

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.email.InputAndEditMailViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentEditMailBinding
import javax.inject.Inject

@AndroidEntryPoint
class EditMailFragment : BaseFragment<FragmentEditMailBinding, EditMailViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_edit_mail

    private val mViewModel: EditMailViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        mViewModel.getEmail()

        binding.edtEmail.addTextChangedListener { mViewModel.checkValidEmail(it.toString().trim()) }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.userEmailLiveData.observe(viewLifecycleOwner) { binding.edtEmail.setText(it) }

        mViewModel.isEnableButtonSave.observe(viewLifecycleOwner) { binding.btnSave.isEnabled = it }

        mViewModel.isShowWrongFormatEmail.observe(viewLifecycleOwner) {
            binding.llItemWrongMailFormat.isVisible = it
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is EditMailActionState.EditMailSuccess -> {
                    binding.btnSave.isEnabled = false
                    appNavigation.openEditMailToVerifyCode(
                        bundleOf(
                            BUNDLE_KEY.CODE_SCREEN to InputAndEditMailViewModel.SCREEN_EDIT_EMAIL,
                            BUNDLE_KEY.EMAIL to it.memberMail,
                            BUNDLE_KEY.FOCUS_EDITTEXT_EMAIL to false
                        )
                    )
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnSave.setOnClickListener {
            mViewModel.updateMail(binding.edtEmail.text.toString().trim())
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }
}
