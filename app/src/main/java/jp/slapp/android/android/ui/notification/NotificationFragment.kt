package jp.slapp.android.android.ui.notification

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentNotificationBinding
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_notification

    private val mViewModel: NotificationViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()
        setUpSwitch()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.statusSwitch.observe(viewLifecycleOwner) {
            binding.scNotification.isChecked = it == PUSH_RECEIVE_NOTIFICATION
        }

        mViewModel.statusSwitchReceiveMail.observe(viewLifecycleOwner) {
            binding.scPermissionToReceiveMail.isChecked = it == PUSH_RECEIVE_MAIL
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

    private fun setUpSwitch() {
        binding.scNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) mViewModel.updateSettingNotification(isChecked)
        }

        binding.scPermissionToReceiveMail.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) mViewModel.updateSettingMail(isChecked)
        }
    }
}
