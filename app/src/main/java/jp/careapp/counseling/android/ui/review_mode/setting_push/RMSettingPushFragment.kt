package jp.careapp.counseling.android.ui.review_mode.setting_push

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentNotificationBinding

@AndroidEntryPoint
class RMSettingPushFragment : BaseFragment<FragmentNotificationBinding, RMSettingPushViewModel>() {
    override val layoutId = R.layout.fragment_notification

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
        binding.apply {
            pushNotification.tvStart.text = getString(R.string.rv_push_notification)
            toolBar.apply {
                setTvTitle(getString(R.string.rv_push_notification_settings))
                setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
                    override fun onClickLeft() {
                        super.onClickLeft()
                        if (!isDoubleClick) findNavController().navigateUp()
                    }
                })
            }
        }
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