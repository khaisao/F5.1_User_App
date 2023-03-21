package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.message.MessageResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentSecretMessageBottomBinding
import javax.inject.Inject

@AndroidEntryPoint
class SecretMessageBottomFragment : BottomSheetDialogFragment() {

    companion object {
        var isShow: Boolean = false
        fun showSecretMessageBottomSheet(
            fragmentManager: FragmentManager,
            itemView: ClickItemView
        ) {
            val bundle = Bundle()
            val fragment = SecretMessageBottomFragment()
            fragment.setClickItemView(itemView)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "")
        }
    }

    @Inject
    lateinit var appNavigation: AppNavigation

    lateinit var type: String

    lateinit var binding: FragmentSecretMessageBottomBinding

    private var sendMessageEnable = false

    private lateinit var adapter: SecretMessageAdapter


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
                R.layout.fragment_secret_message_bottom,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter = SecretMessageAdapter(requireContext())
        binding.messageRv.adapter = adapter
        adapter.submitList(
            listOf(MessageResponse(body = "a"),MessageResponse(body = getString(R.string.message_default_performer)),MessageResponse(body = getString(R.string.message_default_performer), sendMail = true),MessageResponse(body = "a", sendMail = true))
        )
        binding.contentMessageEdt.addTextChangedListener {
            sendMessageEnable = if (TextUtils.isEmpty(it?.toString()?.trim() ?: "")) {
                binding.sendMessageIv.setImageResource(R.drawable.ic_message_inactive)
                false
            } else {
                binding.sendMessageIv.setImageResource(R.drawable.ic_message_send_active)
                true
            }
        }

        binding.messageRv.setOnClickListener {
            DeviceUtil.hideSoftKeyboard(activity)
            if (binding.contentMessageEdt.isFocused) {
                binding.contentMessageEdt.clearFocus()
            }
        }
        setOnClickView()
    }

    private fun setOnClickView() {

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
        fun clickSend()
    }
}