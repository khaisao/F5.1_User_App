package jp.careapp.counseling.android.ui.notification

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentNotificationBinding
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
            binding.scNotification.isChecked = it == PUSH_RECEIVE
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
    }
}
