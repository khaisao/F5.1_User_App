package jp.careapp.counseling.android.ui.buy_point

import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentBuyPointBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
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
