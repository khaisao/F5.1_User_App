package jp.careapp.counseling.android.ui.review_mode.calling

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentRmCallingBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RMCallingFragment : BaseFragment<FragmentRmCallingBinding, RMCallingViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModel: RMCallingViewModel by activityViewModels()

    override val layoutId: Int = R.layout.fragment_rm_calling
    override fun getVM(): RMCallingViewModel = viewModel

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

            Glide.with(this@RMCallingFragment).asGif().load(R.drawable.ic_rm_call_loading).into(binding.ivMotion)
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

        lifecycleScope.launch {
            delay(500)
            openLiveStream()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.performerInfo.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    tvName.text = it.name
                    if (it.imageUrl.isNotEmpty()) {
                        ivAvatar.loadImage(it.imageUrl, false)
                    } else {
                        ivAvatar.loadImage(R.drawable.ic_no_image, false)
                    }
                }
            }
        }
        viewModel.callState.observe(viewLifecycleOwner) {
            it?.let { updateUI(it) }
        }
    }

    private fun openLiveStream() {
        Bundle().also {
//            it.putString(BUNDLE_KEY.PERFORMER_NAME, user.name ?: "")
//            it.putString(BUNDLE_KEY.PERFORMER_CODE, user.code ?: "")
//            it.putString(BUNDLE_KEY.PERFORMER_IMAGE, user.imageUrl ?: "")
        }.let {
            appNavigation.openRMCallingToRMLivestream(it)
        }
    }

    private fun updateUI(state: CallState) {
        when (state) {
            CallState.CONNECTING -> {
                binding.apply {
                    ivMotion.isVisible = true
                }
            }
            CallState.TALKING -> {
                binding.apply {
                    ivMotion.isVisible = false
                }
            }
        }
    }
}