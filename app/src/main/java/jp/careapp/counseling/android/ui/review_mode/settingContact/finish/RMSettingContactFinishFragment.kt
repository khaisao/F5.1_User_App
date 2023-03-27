package jp.careapp.counseling.android.ui.review_mode.settingContact.finish

import android.graphics.Color
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingContactFinishBinding
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
        binding.toolBar.setRootLayoutBackgroundColor(Color.TRANSPARENT)
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }
}