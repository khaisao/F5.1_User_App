package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.screenHeight
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
        // Set the height of cl_main to 50% of the screen height
        val constraintLayout = binding.clMain
        constraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // Set the height of cl_main to 50% of the screen height
                val desiredHeight = (requireContext().screenHeight * 0.5).toInt()
                val layoutParams = constraintLayout.layoutParams
                layoutParams.height = desiredHeight
                constraintLayout.layoutParams = layoutParams
            }
        })
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.messageRv.layoutManager = layoutManager
        adapter = SecretMessageAdapter(requireContext())
        binding.messageRv.adapter = adapter

        // TODO Remove sample data
        adapter.submitList(
            listOf(
                MessageResponse(body = "a"),
                MessageResponse(body = getString(R.string.message_default_performer)),
                MessageResponse(
                    body = getString(R.string.message_default_performer),
                    sendMail = true
                ),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true),
                MessageResponse(body = "a", sendMail = true)
            )
        )
        binding.messageRv.scrollToPosition(adapter.itemCount - 1)
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