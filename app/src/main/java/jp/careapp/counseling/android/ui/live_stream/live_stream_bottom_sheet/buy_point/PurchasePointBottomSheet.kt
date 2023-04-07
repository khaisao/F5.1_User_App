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
import jp.careapp.counseling.databinding.FragmentPurchasePointBottomBinding

@AndroidEntryPoint
class PurchasePointBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentPurchasePointBottomBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: PurchasePointViewModel by viewModels()

    private var purchasePointCallback: PurchasePointCallback? = null
    private var pointUrl: String? = null
    private var type = 0

    companion object {
        var fragment: PurchasePointBottomSheet? = null
        fun showPointBottomSheet(
            fragmentManager: FragmentManager,
            bundle: Bundle? = null,
            purchasePointCallback: PurchasePointCallback? = null
        ) {
            if (fragment == null) {
                fragment = PurchasePointBottomSheet()
                purchasePointCallback?.let { fragment?.setPurchasePointCallback(it) }
                fragment?.arguments = bundle
                fragment?.show(fragmentManager, "PurchasePointBottomSheet")
            }
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
        setOnClick()
        bindingStateView()
    }

    private fun initView() {
        val bundle = arguments
        if (bundle != null) {
            type = bundle.getInt(BUNDLE_KEY.TYPE_BUY_POINT, 0)
            pointUrl = bundle.getString(BUNDLE_KEY.POINT_URL, "")
        }
        when (type) {
            0 -> {
                binding.titlePointTv.text = getString(R.string.point_purchase)
            }
            else -> {
                binding.titlePointTv.text = getString(R.string.insufficient_points)
            }
        }
        pointUrl?.let {
            mViewModel.getPurchasePointConfig(it)
        }
    }

    private fun setOnClick() {
        binding.pointPurchaseButton1.setOnClickListener {

        }
        binding.pointPurchaseButton2.setOnClickListener {

        }
        binding.pointPurchaseButton3.setOnClickListener {

        }
    }

    private fun bindingStateView() {
        mViewModel.fssPurchasePoint.observe(viewLifecycleOwner) {
            binding.pointPurchaseButton1.text =
                getString(R.string.pointButtonText, it.set1.price, it.set1.point)
            binding.pointPurchaseButton2.text =
                getString(R.string.pointButtonText, it.set2.price, it.set2.point)
            binding.pointPurchaseButton3.text =
                getString(R.string.pointButtonText, it.set3.price, it.set3.point)
        }
    }

    fun setPurchasePointCallback(purchasePointCallback: PurchasePointCallback) {
        this.purchasePointCallback = purchasePointCallback
    }

    interface PurchasePointCallback {
        fun purchasePointSuccess()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}