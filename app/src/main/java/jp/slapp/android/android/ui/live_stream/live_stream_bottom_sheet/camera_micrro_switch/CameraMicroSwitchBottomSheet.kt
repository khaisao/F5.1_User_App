package jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.camera_micrro_switch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.R
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentCameraMicroSwitchBottomSheetBinding

@AndroidEntryPoint
class CameraMicroSwitchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentCameraMicroSwitchBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var settingCallback: LiveStreamMicAndCameraChangeCallback? = null

    private val mViewModel: CameraMicroSwitchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraMicroSwitchBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isMicMute = arguments?.getBoolean(BUNDLE_KEY.MIC_SETTING)
        val isCameraMute = arguments?.getBoolean(BUNDLE_KEY.CAMERA_SETTING)
        binding.scMicrophone.isChecked = !(isMicMute ?: true)
        binding.scCamera.isChecked = !(isCameraMute ?: true)

        binding.scMicrophone.setOnCheckedChangeListener { _, isChecked ->
            settingCallback?.onMicChange(!isChecked)
        }

        binding.scCamera.setOnCheckedChangeListener { _, isChecked ->
            settingCallback?.onCameraChange(!isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setCallback(_settingCallback: LiveStreamMicAndCameraChangeCallback?) {
        settingCallback = _settingCallback
    }

    companion object {
        @JvmStatic
        fun newInstance(
            _isMicMute: Boolean,
            _isCameraMute: Boolean,
            _settingCallback: LiveStreamMicAndCameraChangeCallback?
        ) = CameraMicroSwitchBottomSheet().apply {
            val bundle = Bundle()
            bundle.putBoolean(BUNDLE_KEY.CAMERA_SETTING, _isCameraMute)
            bundle.putBoolean(BUNDLE_KEY.MIC_SETTING, _isMicMute)
            arguments = bundle
            setCallback(_settingCallback)
        }
    }
}

interface LiveStreamMicAndCameraChangeCallback {
    fun onMicChange(_isMicMute: Boolean)
    fun onCameraChange(_isCameraMute: Boolean)
}