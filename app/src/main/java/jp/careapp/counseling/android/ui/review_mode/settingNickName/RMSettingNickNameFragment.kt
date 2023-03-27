package jp.careapp.counseling.android.ui.review_mode.settingNickName

import android.graphics.Color
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingNickNameBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingNickNameFragment :
    BaseFragment<FragmentRmSettingNickNameBinding, RMSettingNickNameViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_setting_nick_name

    private val mViewModel: RMSettingNickNameViewModel by viewModels()
    override fun getVM(): RMSettingNickNameViewModel = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.edtNickName.setHint(resources.getString(R.string.nick_name))
        binding.edtNickName.setTextChangeListener { count ->
            val btnRightEnable =
                count != 0 && getInputNickName() != (mViewModel.nickname.value ?: "")
            binding.toolBar.setRightEnable(btnRightEnable)
            binding.toolBar.setColorBtnRight(
                if (btnRightEnable) R.color.white else R.color.color_c0c3c9,
                requireActivity()
            )
            binding.toolBar.setBackgroundBtnRight(
                if (btnRightEnable) R.drawable.bg_rm_btn_toolbar_enable else R.drawable.bg_rm_btn_toolbar_disable
            )
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setRootLayoutBackgroundColor(Color.TRANSPARENT)
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }

            override fun onClickRight() {
                super.onClickRight()
                if (!isDoubleClick) mViewModel.saveNickName(getInputNickName())
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.nickname.observe(viewLifecycleOwner) {
            it?.let {
                if (getInputNickName().isEmpty()) {
                    binding.edtNickName.setText(it)
                }
                binding.toolBar.setRightEnable(false)
            }
        }

        mViewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.SaveNickNameSuccess) {
                if (it.isSuccess) {
                    showDialogSuccess()
                }
            }
        }
    }

    private fun getInputNickName() = binding.edtNickName.getText().trim()

    private fun showDialogSuccess() {
        context?.let { context ->
            CommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_setting_nick_name)
                .setTextPositiveButton(R.string.text_OK)
                .setOnPositivePressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }
        }
    }
}