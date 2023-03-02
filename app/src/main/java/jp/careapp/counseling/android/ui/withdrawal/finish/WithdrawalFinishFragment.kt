package jp.careapp.counseling.android.ui.withdrawal.finish

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentWithdrawalFinishBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalFinishFragment :
    BaseFragment<FragmentWithdrawalFinishBinding, WithdrawalFinishViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_withdrawal_finish

    private val mViewModel: WithdrawalFinishViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.btnGoHome.setOnClickListener { if (!isDoubleClick) appNavigation.openActionToLogin() }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.openActionToLogin()
            }
        })
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        super.onDestroyView()
        appNavigation.openActionToLogin()
    }
}
