package jp.careapp.counseling.android.ui.contact_us.confirm

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.CONTACT_US_MODE
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentContactUsConfirmBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsConfirmFragment :
    BaseFragment<FragmentContactUsConfirmBinding, ContactUsConfirmViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us_confirm

    private val mViewModel: ContactUsConfirmViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        binding.viewModel = mViewModel

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is ContactUsConfirmActionState.SendContactUsSuccess -> appNavigation.openContactUsConfirmToContactUsFinish(
                    bundleOf(CONTACT_US_MODE to it.contactUsMode)
                )
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnSend.setOnClickListener { if (!isDoubleClick) mViewModel.sendContactUs() }

        binding.btnBack.setOnClickListener { if (!isDoubleClick) appNavigation.navigateUp() }
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