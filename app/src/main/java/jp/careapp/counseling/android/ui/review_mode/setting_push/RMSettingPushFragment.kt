package jp.careapp.counseling.android.ui.review_mode.setting_push

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingPushBinding

@AndroidEntryPoint
class RMSettingPushFragment : BaseFragment<FragmentRmSettingPushBinding, RMSettingPushViewModel>() {
    override val layoutId = R.layout.fragment_rm_setting_push

    private val viewModel: RMSettingPushViewModel by viewModels()
    override fun getVM() = viewModel

    override fun initView() {
        super.initView()

        setUpToolBar()
        setUpSwitch()
    }

    private fun setUpSwitch() {
        binding.pushNotification.btnNotifi.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) viewModel.updateSettingPush(isChecked)
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setRootLayoutBackgroundColor(Color.TRANSPARENT)
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) findNavController().navigateUp()
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.pushMail.observe(viewLifecycleOwner) {
            it?.let {
                binding.pushNotification.btnNotifi.isChecked = it == PUSH_RECEIVE
            }
        }
    }

    companion object {
        const val PUSH_DO_NOT_RECEIVE = 0
        const val PUSH_RECEIVE = 1
    }
}