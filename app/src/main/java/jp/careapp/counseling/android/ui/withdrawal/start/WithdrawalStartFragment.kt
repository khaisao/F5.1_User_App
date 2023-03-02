package jp.careapp.counseling.android.ui.withdrawal.start

import android.view.View
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.withdrawal.WithdrawalViewModel
import jp.careapp.counseling.databinding.FragmentWithdrawalStartBinding
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalStartFragment : BaseFragment<FragmentWithdrawalStartBinding, WithdrawalViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModels: WithdrawalViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_withdrawal_start
    override fun getVM(): WithdrawalViewModel = viewModels

    override fun initView() {
        super.initView()
        binding.appBar.btnLeft.apply {
            setOnClickListener {
                if (!isDoubleClick) {
                    appNavigation.navigateUp()
                }
            }
            setImageDrawable(resources.getDrawable(R.drawable.ic_navigation_arrow))
        }
        binding.appBar.viewStatusBar.visibility = View.GONE
        binding.appBar.tvTitle.text = getString(R.string.unsubscribed)
        binding.appBar.tvTitle.setTextColor(requireContext().getColor(R.color.color_text_E3DFEF))
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.rlSignInWithoutEmail.setOnClickListener {
            appNavigation.openWithdrawalStartToEditProfile()
        }
        binding.btnWithdrawal.setOnClickListener {
            appNavigation.openWithdrawalStartToWithdrawal()
        }

    }

    override fun bindingStateView() {
        super.bindingStateView()
    }
}