package jp.careapp.counseling.android.ui.review_mode.settingProfileMessage

import android.graphics.Color
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingProfileMessageBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingProfileMessageFragment :
    BaseFragment<FragmentRmSettingProfileMessageBinding, RMSettingProfileMessageViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_setting_profile_message

    private val mViewModel: RMSettingProfileMessageViewModel by viewModels()
    override fun getVM(): RMSettingProfileMessageViewModel = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.edtProfileMessage.addTextChangedListener {
            val btnRightEnable =
                it.toString().trim().isNotEmpty() && getContent() != (mViewModel.content.value
                    ?: "")
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
                if (!isDoubleClick) mViewModel.saveProfileMessage(getContent())
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.content.observe(viewLifecycleOwner) {
            it?.let {
                if (getContent().isEmpty()) {
                    binding.edtProfileMessage.setText(it)
                }
                binding.toolBar.setRightEnable(false)
            }
        }

        mViewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.SaveProfileMessageSuccess) {
                if (it.isSuccess) {
                    showDialogSuccess()
                }
            }
        }
    }

    private fun getContent() = binding.edtProfileMessage.text.toString().trim()

    private fun showDialogSuccess() {
        context?.let { context ->
            CommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_setting_profile_message)
                .setTextPositiveButton(R.string.text_OK)
                .setOnPositivePressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }
        }
    }
}