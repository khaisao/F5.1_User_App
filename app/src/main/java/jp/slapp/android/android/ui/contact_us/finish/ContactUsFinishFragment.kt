package jp.slapp.android.android.ui.contact_us.finish

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.CONTACT_US_MODE
import jp.slapp.android.android.utils.ContactUsMode
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentContactUsFinishBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsFinishFragment :
    BaseFragment<FragmentContactUsFinishBinding, ContactUsFinishViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us_finish

    private val mViewModel: ContactUsFinishViewModel by viewModels()
    override fun getVM() = mViewModel

    var contactUsMode = ContactUsMode.CONTACT_WITH_MAIL

    override fun initView() {
        super.initView()

        contactUsMode = arguments?.getInt(CONTACT_US_MODE) ?: ContactUsMode.CONTACT_WITH_MAIL
        setUpToolBar()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnBackHome.setOnClickListener {
            if (!isDoubleClick) {
                when (contactUsMode) {
                    ContactUsMode.CONTACT_WITHOUT_MAIL -> appNavigation.navController?.popBackStack(
                        R.id.topFragment,
                        false
                    )
                    ContactUsMode.CONTACT_WITH_MAIL -> appNavigation.navController?.popBackStack(
                        R.id.verificationCodeHelpFragment,
                        false
                    )
                }
            }
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
}