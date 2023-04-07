package jp.careapp.counseling.android.ui.calling

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
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

        binding.apply {
            arguments?.apply {
                val name = getString(BUNDLE_KEY.PERFORMER_NAME) ?: ""
                val performerCode = getString(BUNDLE_KEY.PERFORMER_CODE) ?: ""
                val imageUrl = getString(BUNDLE_KEY.PERFORMER_IMAGE) ?: ""
                viewModel.setPerformerInfo(name, performerCode, imageUrl)
                clear()
            }

            Glide.with(this@CallingFragment).asGif().load(R.drawable.ic_call_loading).into(binding.ivMotion)
            ivEndCall.setOnClickListener {
                if (!isDoubleClick) {
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
                        ivAvatar.loadImage(it.imageUrl, false)
                    } else {
                        ivAvatar.loadImage(R.drawable.ic_avatar_default, false)
                    }
                }
            }
        }
    }
}