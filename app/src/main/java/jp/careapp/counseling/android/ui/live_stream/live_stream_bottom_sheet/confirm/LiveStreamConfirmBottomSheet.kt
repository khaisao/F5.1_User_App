package jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.android.ui.live_stream.LiveStreamFragment
import jp.careapp.counseling.databinding.FragmentLiveStreamConfirmBottomSheetBinding

@AndroidEntryPoint
class LiveStreamConfirmBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentLiveStreamConfirmBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: LiveStreamConfirmViewModel by viewModels()

    private var listener: LiveStreamConfirmBottomSheetDialogListener? = null

    fun setListener(liveStreamConfirmBottomSheetListener: LiveStreamConfirmBottomSheetDialogListener?) {
        listener = liveStreamConfirmBottomSheetListener
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
        _binding = FragmentLiveStreamConfirmBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = mViewModel

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnOk.setOnClickListener {
            listener?.onClickButtonOKConfirmBottomSheet(
                arguments?.getString(
                    LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE
                ) ?: ""
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(
            mode: String,
            listener: LiveStreamConfirmBottomSheetDialogListener? = null
        ) = LiveStreamConfirmBottomSheet().apply {
            arguments = bundleOf(LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE to mode)
            setListener(listener)
        }
    }
}

interface LiveStreamConfirmBottomSheetDialogListener {
    fun onClickButtonOKConfirmBottomSheet(mode: String)
}