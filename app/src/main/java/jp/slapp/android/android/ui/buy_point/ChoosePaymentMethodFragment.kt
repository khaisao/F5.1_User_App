package jp.slapp.android.android.ui.buy_point

import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.databinding.FragmentBuyPointBinding
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Carelear
 * Created by Nguyen Van Vuong on 3/27/21.
 */
@AndroidEntryPoint
class ChoosePaymentMethodFragment :
    BaseFragment<FragmentBuyPointBinding, ChoosPaymentMethodViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_choose_payment_method

    private val viewModel: ChoosPaymentMethodViewModel by viewModels()

    override fun getVM(): ChoosPaymentMethodViewModel = viewModel

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    appNavigation.navigateUp()
                }
            }
        )
    }
}
