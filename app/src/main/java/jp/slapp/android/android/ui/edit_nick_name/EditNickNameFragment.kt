package jp.slapp.android.android.ui.edit_nick_name

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentEditNickNameBinding
import javax.inject.Inject

@AndroidEntryPoint
class EditNickNameFragment : BaseFragment<FragmentEditNickNameBinding, EditNickNameViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_edit_nick_name

    private val mViewModel: EditNickNameViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        mViewModel.getUserName()

        binding.edtNickName.setTextChangeListener { count ->
            mViewModel.checkEnableButtonSave(count, binding.edtNickName.getText())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.userNameLiveData.observe(viewLifecycleOwner) {
            binding.edtNickName.setText(it)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is EditNickNameActionState.EditNickNameSuccess -> {
                    showDialogEditNickNameSuccess()
                    binding.btnSave.isEnabled = false
                }
            }
        }

        mViewModel.isEnableButtonSave.observe(viewLifecycleOwner) {
            binding.btnSave.isEnabled = it
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnSave.setOnClickListener { if (!isDoubleClick) mViewModel.editMemberName(binding.edtNickName.getText()) }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    private fun showDialogEditNickNameSuccess() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.updated_profile)
            .setTextOkButton(R.string.text_OK)
            .setOnOkButtonPressed {
                it.dismiss()
                appNavigation.navigateUp()
            }
    }
}