package jp.careapp.counseling.android.ui.buy_point

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.model.buy_point.ItemPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUY_POINT
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentBuyPointBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BuyPointFragment : BaseFragment<FragmentBuyPointBinding, BuyPointViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_buy_point

    private val viewModel: BuyPointViewModel by viewModels()

    override fun getVM(): BuyPointViewModel = viewModel

    @Inject
    lateinit var rxPreferences: RxPreferences

    private var isFromNotification = false

    lateinit var mAdapter: BuyPointsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadMemberInfo()
    }

    override fun initView() {
        super.initView()
        val bundle = arguments
        formatPoint(rxPreferences.getPoint())
        initAdapter(rxPreferences.getTimeBuy() == 0L)
        if (bundle != null) {
            if (bundle.containsKey(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION)) {
                isFromNotification =
                    bundle.getBoolean(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION, false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.connectBilling()
    }

    private fun initAdapter(firstBuy: Boolean) {
        mAdapter = BuyPointsAdapter { item ->
            if (!isDoubleClick) {
                activity?.let { viewModel.startBilling(item, it) }
            }
        }
        val item1 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_1.id,
            firstBuy,
            BUY_POINT.FIST_BUY_COIN_1.pointCountFirst,
            BUY_POINT.FIST_BUY_COIN_1.costFirst,
            BUY_POINT.FIST_BUY_COIN_1.money
        )
        val item2 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_2.id,
            firstBuy,
            BUY_POINT.FIST_BUY_COIN_2.pointCountFirst,
            BUY_POINT.FIST_BUY_COIN_2.costFirst,
            BUY_POINT.FIST_BUY_COIN_2.money
        )
        val item3 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_3.id,
            firstBuy,
            BUY_POINT.FIST_BUY_COIN_3.pointCountFirst,
            BUY_POINT.FIST_BUY_COIN_3.costFirst,
            BUY_POINT.FIST_BUY_COIN_3.money
        )
        val item4 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_4.id,
            firstBuy,
            BUY_POINT.FIST_BUY_COIN_4.pointCountFirst,
            BUY_POINT.FIST_BUY_COIN_4.costFirst,
            BUY_POINT.FIST_BUY_COIN_4.money
        )
        mAdapter.run { submitList(listOf(item1, item2, item3, item4)) }
        binding.costPointRv.adapter = mAdapter
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.statusBuyPoint.observe(viewLifecycleOwner, handleBuyPointResult)
        viewModel.memberInFoResult.observe(viewLifecycleOwner, handleMemberInfoResult)
    }

    private var handleBuyPointResult: Observer<Boolean> = Observer {
        if (it) {
            initAdapter(false)
            CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                .showDialog()
                .setDialogTitle(R.string.buy_point_suscess)
                .setTextPositiveButton(R.string.text_OK)
                .setOnPositivePressed {
                    viewModel.isLoading.value = false
                    it.dismiss()
                }
            viewModel.statusBuyPoint.value = false
            viewModel.loadMemberInfo()
        }
    }
    private var handleMemberInfoResult: Observer<MemberResponse?> = Observer {
        it?.let { data ->
            rxPreferences.saveMemberInfo(data)
            formatPoint(data.point)
        }
    }

    private fun formatPoint(point: Int) {
        val symbols = DecimalFormatSymbols(Locale(","))
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3
        binding.tvPoint.text = String.format(getString(R.string.format_point_pts), df.format(point))
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    if (isFromNotification) {

                        appNavigation.openBuyPointToTopScreen()
                    } else {
                        appNavigation.navigateUp()
                    }
                }

                override fun onClickRight() {
                    super.onClickRight()

                    if (isFromNotification) {
                        appNavigation.openBuyPointToTopScreen()
                    } else {
                        appNavigation.navigateUp()
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        viewModel.isLoading.value = false
        super.onDestroyView()
    }
}
