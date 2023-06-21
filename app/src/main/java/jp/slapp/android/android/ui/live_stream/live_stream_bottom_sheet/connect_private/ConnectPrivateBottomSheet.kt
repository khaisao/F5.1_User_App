package jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.connect_private

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.R
import jp.slapp.android.android.ui.live_stream.LiveStreamFragment
import jp.slapp.android.databinding.FragmentConnectPrivateBottomSheetBinding

@AndroidEntryPoint
class ConnectPrivateBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentConnectPrivateBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: ConnectPrivateBottomSheetViewModel by viewModels()

    private var listener: LiveStreamConnectPrivateListener? = null

    fun setListener(listener: LiveStreamConnectPrivateListener?) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        if (parentFragment is LiveStreamFragment) {
            listener = parentFragment as LiveStreamFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectPrivateBottomSheetBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).asGif().load(R.drawable.ic_call_loading)
            .into(binding.imageView7)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        binding.btnCancel.setOnClickListener {
            dismiss()
            listener?.onClickButtonCancelConnectPrivate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: LiveStreamConnectPrivateListener) =
            ConnectPrivateBottomSheet().apply {
                setListener(listener)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface LiveStreamConnectPrivateListener {
    fun onClickButtonCancelConnectPrivate()
}