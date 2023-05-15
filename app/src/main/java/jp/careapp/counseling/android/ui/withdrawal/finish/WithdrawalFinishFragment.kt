package jp.careapp.counseling.android.ui.withdrawal.finish

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentWithdrawalFinishBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalFinishFragment :
    BaseFragment<FragmentWithdrawalFinishBinding, WithdrawalFinishViewModel>() {

    private val shareViewModel: ShareViewModel by activityViewModels()

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_withdrawal_finish

    private val mViewModel: WithdrawalFinishViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.btnGoHome.setOnClickListener {
            if (!isDoubleClick) {
                shareViewModel.setTabSelected(-1)
                val bundle = Bundle().apply {
                    putInt(
                        BUNDLE_KEY.CODE_SCREEN,
                        InputAndEditMailViewModel.SCREEN_LOGIN_WITH_EMAIL
                    )
                }
                appNavigation.openActionToLoginAndClearBackstack(bundle)
            }
        }

    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.openActionToLogin()
            }
        })
    }

}
