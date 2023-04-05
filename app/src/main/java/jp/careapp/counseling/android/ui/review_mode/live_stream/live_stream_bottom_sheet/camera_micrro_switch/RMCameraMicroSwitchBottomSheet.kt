package jp.careapp.counseling.android.ui.review_mode.live_stream.live_stream_bottom_sheet.camera_micrro_switch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentRmCameraMicroSwitchBottomSheetBinding

@AndroidEntryPoint
class RMCameraMicroSwitchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRmCameraMicroSwitchBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: RMCameraMicroSwitchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRmCameraMicroSwitchBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}