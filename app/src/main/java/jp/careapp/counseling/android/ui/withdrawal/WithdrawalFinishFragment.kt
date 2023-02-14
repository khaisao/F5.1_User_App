package jp.careapp.counseling.android.ui.withdrawal

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.databinding.FragmentWithdrawalFinishBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalFinishFragment :
    BaseFragment<FragmentWithdrawalFinishBinding, WithdrawalViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModels: WithdrawalViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_withdrawal_finish
    override fun getVM(): WithdrawalViewModel = viewModels

    override fun initView() {
        super.initView()
        viewModels.saveListCategory()
    }

    @Inject
    lateinit var appPreferences: RxPreferences
    override fun bindingStateView() {
        super.bindingStateView()
        binding.apply {
            appBar.apply {
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        appNavigation.openActionToLogin()
                }
                tvTitle.text = getString(R.string.unsubscribed)
                viewStatusBar.visibility = View.GONE
            }
        }
        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        super.onDestroyView()
        appNavigation.openActionToLogin()
    }
}
