package jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.rm_connect_private

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentRmConnectPrivateBottomSheetBinding

@AndroidEntryPoint
class RMConnectPrivateBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRmConnectPrivateBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: RMConnectPrivateBottomSheetViewModel by viewModels()

    private var listener: RMLiveStreamConnectPrivateListener? = null

    fun setListener(listener: RMLiveStreamConnectPrivateListener?) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
//
//        if (parentFragment is RMLiveStreamFragment) {
//            listener = parentFragment as RMLiveStreamFragment
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRmConnectPrivateBottomSheetBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).asGif().load(R.drawable.ic_rm_call_loading)
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
        fun newInstance(listener: RMLiveStreamConnectPrivateListener) =
            RMConnectPrivateBottomSheet().apply {
                setListener(listener)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface RMLiveStreamConnectPrivateListener {
    fun onClickButtonCancelConnectPrivate()
}