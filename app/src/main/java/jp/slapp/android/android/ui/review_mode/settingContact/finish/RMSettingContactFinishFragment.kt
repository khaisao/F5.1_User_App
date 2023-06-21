package jp.slapp.android.android.ui.review_mode.settingContact.finish

import android.graphics.Color
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentRmSettingContactFinishBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingContactFinishFragment :
    BaseFragment<FragmentRmSettingContactFinishBinding, RMSettingContactFinishViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_setting_contact_finish

    private val mViewModel: RMSettingContactFinishViewModel by viewModels()
    override fun getVM(): RMSettingContactFinishViewModel = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }
}