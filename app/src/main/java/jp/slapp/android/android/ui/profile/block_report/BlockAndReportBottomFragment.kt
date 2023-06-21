package jp.slapp.android.android.ui.profile.block_report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.databinding.FragmentBlockReportBottomBinding
import javax.inject.Inject

@AndroidEntryPoint
class BlockAndReportBottomFragment : BottomSheetDialogFragment() {

    companion object {
        var isShow: Boolean = false
        fun showBlockAndReportBottomSheet(
            fragmentManager: FragmentManager,
            itemView: ClickItemView
        ) {
            val bundle = Bundle()
            val fragment = BlockAndReportBottomFragment()
            fragment.setClickItemView(itemView)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "")
        }
    }

    @Inject
    lateinit var appNavigation: AppNavigation

    lateinit var type: String

    lateinit var binding: FragmentBlockReportBottomBinding

    override fun onStart() {
        super.onStart()
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
            bottomSheetBehavior?.peekHeight = view.measuredHeight
            try {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior?.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                                isShow = true
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
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_block_report_bottom,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.cancelButtonTv.setOnClickListener {
            dismiss()
            isShow = false
        }
        binding.blockLl.setOnClickListener {
            dismiss()
            clickItemView.clickBlock()
            isShow = false
        }
        binding.reportLl.setOnClickListener {
            dismiss()
            clickItemView.clickReport()
            isShow = false
        }
        binding.container.setOnClickListener {
            dismiss()
            isShow = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isShow = false
    }

    private lateinit var clickItemView: ClickItemView
    fun setClickItemView(clickItemView: ClickItemView) {
        this.clickItemView = clickItemView
    }

    interface ClickItemView {
        fun clickBlock()
        fun clickReport()
    }
}
