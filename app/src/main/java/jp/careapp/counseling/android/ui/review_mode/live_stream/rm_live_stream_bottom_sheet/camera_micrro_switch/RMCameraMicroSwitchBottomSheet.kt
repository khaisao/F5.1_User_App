package jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_bottom_sheet.camera_micrro_switch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentRmCameraMicroSwitchBottomSheetBinding

@AndroidEntryPoint
class RMCameraMicroSwitchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRmCameraMicroSwitchBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var settingCallback: RMLiveStreamMicAndCameraChangeCallback? = null

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

    fun setCallback(_settingCallback: RMLiveStreamMicAndCameraChangeCallback?) {
        settingCallback = _settingCallback
    }

    companion object {
        @JvmStatic
        fun newInstance(
            _isMicMute: Boolean,
            _isCameraMute: Boolean,
            _settingCallback: RMLiveStreamMicAndCameraChangeCallback?
        ) = RMCameraMicroSwitchBottomSheet().apply {
            val bundle = Bundle()
            bundle.putBoolean(BUNDLE_KEY.CAMERA_SETTING, _isCameraMute)
            bundle.putBoolean(BUNDLE_KEY.MIC_SETTING, _isMicMute)
            arguments = bundle
            setCallback(_settingCallback)
        }
    }
}

interface RMLiveStreamMicAndCameraChangeCallback {
    fun onMicChange(_isMicMute: Boolean)
    fun onCameraChange(_isCameraMute: Boolean)
}