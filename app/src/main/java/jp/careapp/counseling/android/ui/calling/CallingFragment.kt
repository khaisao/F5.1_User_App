package jp.careapp.counseling.android.ui.calling

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.loadImage
import jp.careapp.core.utils.loadImageAndBlur
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.extensions.setAllOnClickListener
import jp.careapp.counseling.android.utils.extensions.toDurationTime
import jp.careapp.counseling.databinding.FragmentCallingBinding
import javax.inject.Inject

@AndroidEntryPoint
class CallingFragment : BaseFragment<FragmentCallingBinding, CallingViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModel: CallingViewModel by activityViewModels()

    override val layoutId: Int = R.layout.fragment_calling
    override fun getVM(): CallingViewModel = viewModel

    override fun initView() {
        super.initView()

        viewModel.showMinimizeCall(false)

        binding.apply {
            arguments?.apply {
                val name = getString(BUNDLE_KEY.PERFORMER_NAME) ?: ""
                val performerCode = getString(BUNDLE_KEY.PERFORMER_CODE) ?: ""
                val imageUrl = getString(BUNDLE_KEY.PERFORMER_IMAGE) ?: ""
                viewModel.setPerformerInfo(name, performerCode, imageUrl)
                clear()
            }

            ivMotion.loadImage(R.drawable.ic_motion_calling)

            ivMinimum.setOnClickListener {
                if (!isDoubleClick) {
                    viewModel.showMinimizeCall(true)
                    appNavigation.navigateUp()
                }
            }
            groupSheet.setAllOnClickListener {
                if (!isDoubleClick) {
                    viewModel.showMinimizeCall(true)
                    appNavigation.openCallingToUpdateTroubleSheet()
                }
            }
            groupMessage.setAllOnClickListener {
                if (!isDoubleClick) {
                    viewModel.showMinimizeCall(true)
                    appNavigation.containScreenInBackStack(R.id.chatMessageFragment) { isContain, _ ->
                        if (isContain) {
                            appNavigation.popopBackStackToDetination(R.id.chatMessageFragment)
                        } else {
                            openChatMessage()
                        }
                    }
                }
            }
            groupBuyPoint.setAllOnClickListener {
                if (!isDoubleClick) {
                    viewModel.showMinimizeCall(true)

                    if (viewModel.isFullMode()) {
                        appNavigation.navigateUp()
                        val bundle = Bundle().apply {
                            putString(Define.TITLE_WEB_VIEW, getString(R.string.buy_point))
                            putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
                        }
                        appNavigation.openScreenToWebview(bundle)
                    } else {
                        appNavigation.openCallingToBuyPoint()
                    }
                }
            }
            groupMic.setAllOnClickListener {
                if (!isDoubleClick) {
                    viewModel.changeMic()
                }
            }
            groupSpeaker.setAllOnClickListener {
                if (!isDoubleClick) {
                    viewModel.changeSpeaker()
                }
            }
            ivEndCall.setOnClickListener {
                if (!isDoubleClick) {
                    viewModel.onEndCall()
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.showMinimizeCall(true)
                appNavigation.navigateUp()
            }
        })
    }

    private fun openChatMessage() {
        val performerInfo = viewModel.getPerformerInfo()
        Bundle().also {
            it.putString(BUNDLE_KEY.PERFORMER_NAME, performerInfo.name)
            it.putString(BUNDLE_KEY.PERFORMER_CODE, performerInfo.performerCode)
            it.putBoolean(BUNDLE_KEY.IS_OPEN_FROM_CALLING, true)
        }.let {
            appNavigation.openCallingToChatMessage(it)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.performerInfo.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    tvName.text = it.name
                    if (it.imageUrl.isNotEmpty()) {
                        ivAvatar.loadImage(it.imageUrl, true)
                        ivBackground.loadImageAndBlur(it.imageUrl)
                    } else {
                        ivAvatar.loadImage(R.drawable.ic_avatar_default, true)
                        ivBackground.loadImageAndBlur(R.drawable.ic_avatar_default)
                    }
                }
            }
        }
        viewModel.callState.observe(viewLifecycleOwner) {
            it?.let { updateUI(it) }
        }
        viewModel.isMuteMic.observe(viewLifecycleOwner) {
            it?.let { changeMic(it) }
        }
        viewModel.isMuteSpeaker.observe(viewLifecycleOwner) {
            it?.let { changeSpeaker(it) }
        }
        viewModel.callDuration.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvDuration.text = it.toDurationTime()
            }
        }
    }

    private fun updateUI(state: CallState) {
        when (state) {
            CallState.CONNECTING -> {
                binding.apply {
                    ivMotion.isVisible = true
                    tvDuration.isInvisible = true
                    groupMic.isInvisible = true
                    groupSpeaker.isInvisible = true
                }
            }
            CallState.TALKING -> {
                binding.apply {
                    ivMotion.isVisible = false
                    tvDuration.isInvisible = false
                    groupMic.isInvisible = false
                    groupSpeaker.isInvisible = false
                }
            }
        }
    }

    private fun changeMic(isMute: Boolean) {
        binding.apply {
            if (isMute) {
                ivMic.loadImage(R.drawable.ic_mic_off)
                tvMic.text = getString(R.string.mic_on)
            } else {
                ivMic.loadImage(R.drawable.ic_mic_on)
                tvMic.text = getString(R.string.mic_off)
            }
        }
    }

    private fun changeSpeaker(isMute: Boolean) {
        binding.apply {
            if (isMute) {
                ivSpeaker.loadImage(R.drawable.ic_speaker_off)
                tvSpeaker.text = getString(R.string.speaker_on)
            } else {
                ivSpeaker.loadImage(R.drawable.ic_speaker_on)
                tvSpeaker.text = getString(R.string.speaker_off)
            }
        }
    }
}