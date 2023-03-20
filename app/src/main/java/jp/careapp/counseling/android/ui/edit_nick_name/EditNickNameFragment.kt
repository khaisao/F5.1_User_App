package jp.careapp.counseling.android.ui.edit_nick_name

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentEditNickNameBinding
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

        binding.edtNickName.setTextChangeListener { count ->
            binding.btnSave.isEnabled = count != 0 && binding.edtNickName.getText().isNotBlank()
        }

        binding.btnSave.setOnClickListener { if (!isDoubleClick) mViewModel.editMemberName(binding.edtNickName.getText()) }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.memberNickName.observe(viewLifecycleOwner) {
            binding.edtNickName.setText(it)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is EditNickNameActionState.EditNickNameSuccess -> showDialogEditNickNameSuccess()
            }
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