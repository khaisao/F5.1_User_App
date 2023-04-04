package jp.careapp.counseling.android.ui.review_mode.setting_profile_message

import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.RMCommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.android.utils.replaceSystemWindowInsets
import jp.careapp.counseling.android.utils.showSoftKeyboard
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

        binding.root.replaceSystemWindowInsets()

        setUpToolBar()

        binding.edtProfileMessage.requestFocus()
        binding.edtProfileMessage.postDelayed(
            { requireActivity().showSoftKeyboard(binding.edtProfileMessage) },
            200
        )

        binding.edtProfileMessage.addTextChangedListener {
            val content = it.toString().trim()
            binding.toolBar.enableBtnRight(content.isNotEmpty() && content != (mViewModel.content.value ?: ""))
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
            RMCommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(R.string.msg_title_setting_profile_message)
                .setTextOkButton(R.string.text_OK)
                .setOnOkButtonPressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}