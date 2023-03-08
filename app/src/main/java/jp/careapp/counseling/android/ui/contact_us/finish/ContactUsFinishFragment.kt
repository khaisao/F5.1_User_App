package jp.careapp.counseling.android.ui.contact_us.finish

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentContactUsFinishBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsFinishFragment :
    BaseFragment<FragmentContactUsFinishBinding, ContactUsFinishViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us_finish

    private val mViewModel: ContactUsFinishViewModel by viewModels()
    override fun getVM() = mViewModel

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