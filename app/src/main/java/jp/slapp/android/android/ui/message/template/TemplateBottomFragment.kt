package jp.slapp.android.android.ui.message.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.R
import jp.slapp.android.android.data.network.FreeTemplateResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentTemplateBottomBinding
import javax.inject.Inject

@AndroidEntryPoint
class TemplateBottomFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var rxPreferences: RxPreferences

    lateinit var binding: FragmentTemplateBottomBinding
    private val mAdapter: TemplateAdapter by lazy {
        TemplateAdapter(
            requireContext(),
            onClickListener = { message ->
                clickItemView.clickItem(message)
            }
        )
    }

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
            bottomSheetBehavior?.setPeekHeight(view.measuredHeight)
            try {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior?.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_template_bottom,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            if (containsKey(BUNDLE_KEY.LIST_TEMPLATE)) {
                val listTemplate =
                    getSerializable(BUNDLE_KEY.LIST_TEMPLATE) as ArrayList<FreeTemplateResponse>
                mAdapter.run { submitList(listTemplate) }

                (binding.itemTemplateRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
                binding.itemTemplateRv.adapter = mAdapter

                setOnClickView()
            }
        }
    }

    private fun setOnClickView() {
        binding.cancelButtonTv.setOnClickListener {
            dismiss()
        }
        binding.container.setOnClickListener {
            dismiss()
        }
    }

    private lateinit var clickItemView: ClickItemView
    fun setClickItemView(clickItemView: ClickItemView) {
        this.clickItemView = clickItemView
    }

    companion object {
        fun showTemplateBottomSheet(
            fragmentManager: FragmentManager,
            bundle: Bundle,
            itemView: ClickItemView,
        ): TemplateBottomFragment {
            val fragment = TemplateBottomFragment()
            fragment.setClickItemView(itemView)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "")
            return fragment
        }
    }

    interface ClickItemView {
        fun clickItem(template: FreeTemplateResponse)
    }
}