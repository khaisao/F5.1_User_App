package jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE
import jp.careapp.counseling.android.ui.live_stream.live_stream_bottom_sheet.confirm.LiveStreamConfirmViewModel
import jp.careapp.counseling.android.ui.review_mode.live_stream.RMLiveStreamFragment
import jp.careapp.counseling.databinding.FragmentRmLiveStreamConfirmBottomSheetBinding

@AndroidEntryPoint
class RMLiveStreamConfirmBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRmLiveStreamConfirmBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: LiveStreamConfirmViewModel by viewModels()

    private var listener: RMLiveStreamConfirmBottomSheetDialogListener? = null

    fun setListener(liveStreamConfirmBottomSheetListener: RMLiveStreamConfirmBottomSheetDialogListener?) {
        listener = liveStreamConfirmBottomSheetListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        if (parentFragment is RMLiveStreamFragment) {
            listener = parentFragment as RMLiveStreamFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRmLiveStreamConfirmBottomSheetBinding.inflate(inflater, container, false)
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
            listener: RMLiveStreamConfirmBottomSheetDialogListener? = null
        ) = RMLiveStreamConfirmBottomSheet().apply {
            arguments = bundleOf(LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE to mode)
            setListener(listener)
        }
    }
}

interface RMLiveStreamConfirmBottomSheetDialogListener {
    fun onClickButtonOKConfirmBottomSheet(mode: String)
}