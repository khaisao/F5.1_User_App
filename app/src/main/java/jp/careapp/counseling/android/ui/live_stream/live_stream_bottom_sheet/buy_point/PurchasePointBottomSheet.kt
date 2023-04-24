package jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.buy_point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define.Companion.BUY_POINT_UNDER_500
import jp.careapp.counseling.android.utils.Define.Companion.INSU_POINT
import jp.careapp.counseling.databinding.FragmentPurchasePointBottomBinding

@AndroidEntryPoint
class PurchasePointBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentPurchasePointBottomBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: PurchasePointViewModel by viewModels()

    private lateinit var purchasePointAdapter: PurchasePointAdapter

    private var purchasePointCallback: PurchasePointCallback? = null
    private var type = 1

    companion object {
        var fragment: PurchasePointBottomSheet? = null
        fun showPointBottomSheet(
            fragmentManager: FragmentManager,
            bundle: Bundle? = null,
            purchasePointCallback: PurchasePointCallback? = null
        ) {
            val fragment = PurchasePointBottomSheet()
            purchasePointCallback?.let { fragment.setPurchasePointCallback(it) }
            fragment.arguments = bundle
            fragment.show(fragmentManager, "PurchasePointBottomSheet")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePointBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindingStateView()
    }

    private fun initView() {
        val bundle = arguments
        if (bundle != null) {
            type = bundle.getInt(BUNDLE_KEY.TYPE_BUY_POINT, 0)
        }
        when (type) {
            INSU_POINT -> {
                binding.titlePointTv.text = getString(R.string.point_purchase)
            }
            BUY_POINT_UNDER_500 -> {
                binding.titlePointTv.text = getString(R.string.point_purchase_under_500)
            }
            else -> {
                binding.titlePointTv.text = getString(R.string.point_purchase_under_1000)
            }
        }
        mViewModel.getCreditPrices()
        purchasePointAdapter = PurchasePointAdapter {
            purchasePointCallback?.onPointItemClick(it.buyPoint ?: 0, it.price ?: 0)
            dismiss()
        }
        binding.costPointRv.adapter = purchasePointAdapter
    }

    private fun bindingStateView() {
        mViewModel.listPointItem.observe(viewLifecycleOwner) {
            purchasePointAdapter.run {
                submitList(it)
            }
        }
    }

    fun setPurchasePointCallback(purchasePointCallback: PurchasePointCallback) {
        this.purchasePointCallback = purchasePointCallback
    }

    interface PurchasePointCallback {
        fun onPointItemClick(point: Int, money: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}