package jp.careapp.counseling.android.ui.review_mode.setting_push

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingPushBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingPushFragment : BaseFragment<FragmentRmSettingPushBinding, RMSettingPushViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_rm_setting_push

    private val viewModel: RMSettingPushViewModel by viewModels()
    override fun getVM() = viewModel

    override fun initView() {
        super.initView()

        setUpToolBar()
        setUpSwitch()
    }

    private fun setUpSwitch() {
        binding.scNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) viewModel.updateSettingPush(isChecked)
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

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.pushMail.observe(viewLifecycleOwner) {
            binding.scNotification.isChecked = it == PUSH_RECEIVE
        }
    }

    companion object {
        const val PUSH_DO_NOT_RECEIVE = 0
        const val PUSH_RECEIVE = 1
    }
}