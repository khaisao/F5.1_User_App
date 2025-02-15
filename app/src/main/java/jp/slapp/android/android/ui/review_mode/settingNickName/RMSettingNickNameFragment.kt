package jp.slapp.android.android.ui.review_mode.settingNickName

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentRmSettingNickNameBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingNickNameFragment :
    BaseFragment<FragmentRmSettingNickNameBinding, RMSettingNickNameViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_setting_nick_name

    private val mViewModel: RMSettingNickNameViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.edtNickName.setHint(resources.getString(R.string.nick_name))
        binding.edtNickName.setTextChangeListener { count ->
            val btnRightEnable = count != 0 && getInputNickName() != (mViewModel.nickname.value ?: "")
            binding.toolBar.enableBtnRight(btnRightEnable)
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }

            override fun onClickRight() {
                super.onClickRight()
                if (!isDoubleClick) mViewModel.updateNickName(getInputNickName())
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.nickname.observe(viewLifecycleOwner) {
            binding.edtNickName.setText(it)
            binding.toolBar.setRightEnable(false)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is RMSettingNickNameActionState.UpdateNickNameSuccess -> showDialogSuccess()
            }
        }
    }

    private fun getInputNickName() = binding.edtNickName.getText().trim()

    private fun showDialogSuccess() {
        context?.let { context ->
            RMCommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_setting_nick_name)
                .setTextPositiveButton(R.string.text_OK)
                .setOnPositivePressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }
        }
    }
}