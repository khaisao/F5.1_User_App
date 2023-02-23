package jp.careapp.counseling.android.ui.review_mode.userDetailReport

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmUserDetailReportBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMUserDetailReportFragment :
    BaseFragment<FragmentRmUserDetailReportBinding, RMUserDetailReportViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_user_detail_report

    private val mViewModel: RMUserDetailReportViewModel by viewModels()
    override fun getVM(): RMUserDetailReportViewModel = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.edtReasonReport.addTextChangedListener {
            binding.toolBar.setRightEnable(it.toString().trim().isNotEmpty())
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }

            override fun onClickRight() {
                super.onClickRight()
                val content = binding.edtReasonReport.text.toString().trim()
                if (!isDoubleClick) mViewModel.sendReportUser(content)
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.SendReportSuccess) {
                if (it.isSuccess) {
                    appNavigation.navigateUp()
                }
            }
        }
    }
}