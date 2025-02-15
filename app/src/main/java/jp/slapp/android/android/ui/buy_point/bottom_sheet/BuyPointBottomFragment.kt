package jp.slapp.android.android.ui.buy_point.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.data.model.user_profile.ActionLoadProfile
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.model.buy_point.ItemPoint
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.buy_point.BuyPointViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.BUY_POINT
import jp.slapp.android.android.utils.Define
import jp.slapp.android.android.utils.formatDecimalSeparator
import jp.slapp.android.databinding.FragmentBuyPointBottomBinding
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BuyPointBottomFragment : BottomSheetDialogFragment() {

    companion object {
        var fragment: BuyPointBottomFragment? = null
        fun showPointBottomSheet(
            fragmentManager: FragmentManager,
            bundle: Bundle? = null,
            handleBuyPoint: HandleBuyPoint? = null
        ) {
            if (fragment == null) {
                fragment = BuyPointBottomFragment()
                handleBuyPoint?.let { fragment?.setHandleBuyPoint(handleBuyPoint) }
                fragment?.arguments = bundle
                fragment?.show(fragmentManager, "")
            }
        }
    }

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var appNavigation: AppNavigation

    lateinit var type: String

    lateinit var mAdapter: BuyPointsBottomSheetAdapter

    private var nextToScreen = ""

    lateinit var binding: FragmentBuyPointBottomBinding

    private val viewModel: BuyPointViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.connectBilling()

        val dialog = dialog

        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val view = view
        requireView().post {
            val parent = view?.parent as View
            val params =
                parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior?.setPeekHeight(view.measuredHeight)
            try {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior?.addBottomSheetCallback(
                    object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    }
                )
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_buy_point_bottom, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAdapter(rxPreferences.getTimeBuy() == 0L)
        setOnClickView()
        bindingStateView()
    }

    fun initView() {
        var typeScreen = Define.INSU_POINT
        nextToScreen = ""
        var bundle = arguments
        if (bundle != null) {
            typeScreen = bundle.getInt(BUNDLE_KEY.TYPE_BUY_POINT, -1)
            nextToScreen = bundle.getString(BUNDLE_KEY.NEXT_TO_SCREEN, "")
        }
        when (typeScreen) {
            Define.BUY_POINT_FIRST -> {
                binding.tvTitle.text = getString(R.string.point_purchase)
            }
            else -> {
                binding.tvTitle.text = getString(R.string.insufficient_points)
                binding.tvContent.text =
                    "${getString(R.string.current_points)}：${formatPoint(rxPreferences.getPoint())}pts"
            }
        }
        formatPoint(rxPreferences.getPoint())
    }

    private fun initAdapter(firstBuy: Boolean) {
        mAdapter = BuyPointsBottomSheetAdapter { item ->
            activity?.let {
                if (it is BaseActivity<*, *> && !it.isDoubleClick) {
                    buyPoint(item, it)
                }
            }
        }
        val item1 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_1.id,
            (BUY_POINT.FIST_BUY_COIN_1.pointCount + BUY_POINT.FIST_BUY_COIN_1.pointBonus).formatDecimalSeparator()
                .plus(getString(R.string.point_unit)),
            BUY_POINT.FIST_BUY_COIN_1.money,
        )
        val item2 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_2.id,
            (BUY_POINT.FIST_BUY_COIN_2.pointCount + BUY_POINT.FIST_BUY_COIN_2.pointBonus).formatDecimalSeparator()
                .plus(getString(R.string.point_unit)),
            BUY_POINT.FIST_BUY_COIN_2.money,
        )
        val item3 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_3.id,
            (BUY_POINT.FIST_BUY_COIN_3.pointCount + BUY_POINT.FIST_BUY_COIN_3.pointBonus).formatDecimalSeparator()
                .plus(getString(R.string.point_unit)),
            BUY_POINT.FIST_BUY_COIN_3.money,
        )
        val item4 = ItemPoint(
            BUY_POINT.FIST_BUY_COIN_4.id,
            (BUY_POINT.FIST_BUY_COIN_4.pointCount + BUY_POINT.FIST_BUY_COIN_4.pointBonus).formatDecimalSeparator()
                .plus(getString(R.string.point_unit)),
            BUY_POINT.FIST_BUY_COIN_4.money,
        )
        mAdapter.run { submitList(listOf(item1, item2, item3, item4)) }
        binding.costPointRv.adapter = mAdapter
    }

    private fun buyPoint(item: ItemPoint, activity: FragmentActivity) {
        activity.let { viewModel.startBilling(item, it) }
    }

    private val isFirstBuyCredit: Boolean
        get() = rxPreferences.getFirstBuyCredit()

    private fun setOnClickView() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.container.setOnClickListener { dismiss() }
    }

    fun bindingStateView() {
        viewModel.isLoading.observe(viewLifecycleOwner, handleLoaing)
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
                    it.dismiss()
                    viewModel.isLoading.value = false
                    BuyPointBottomFragment@ this.dismiss()
                    handleBuyPoint?.buyPointSucess()
                }
            viewModel.statusBuyPoint.value = false
            viewModel.loadMemberInfo()
            EventBus.getDefault().post(ActionLoadProfile(true))
        }
    }

    private var handleMemberInfoResult: Observer<MemberResponse?> = Observer {
        it?.let { data ->
            rxPreferences.saveMemberInfo(data)
            formatPoint(data.point)
        }
    }

    private var handleLoaing: Observer<Boolean> = Observer {
        if (it) {
            (activity as BaseActivity<*, *>?)?.showLoading()
        } else {
            (activity as BaseActivity<*, *>?)?.hiddenLoading()
        }
    }

    fun formatPoint(point: Int) {
        val symbols = DecimalFormatSymbols(Locale(","))
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3

    }

    private var handleBuyPoint: HandleBuyPoint? = null
    fun setHandleBuyPoint(handleBuyPoint: HandleBuyPoint) {
        this.handleBuyPoint = handleBuyPoint
    }

    interface HandleBuyPoint {
        fun buyPointSucess()
    }

    override fun onDestroyView() {
        viewModel.isLoading.value = false
        super.onDestroyView()
    }

    override fun onDestroy() {
        fragment = null
        super.onDestroy()
    }
}
