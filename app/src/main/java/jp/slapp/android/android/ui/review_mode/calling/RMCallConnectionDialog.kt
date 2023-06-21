package jp.slapp.android.android.ui.review_mode.calling

import android.os.Bundle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseDialogFragment
import jp.careapp.core.utils.loadImage
import jp.slapp.android.R
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.SocketInfo
import jp.slapp.android.databinding.FragmentRmCallingBinding

@AndroidEntryPoint
class RMCallConnectionDialog : BaseDialogFragment<FragmentRmCallingBinding>() {

    private var callingCancelListener: CallingCancelListener? = null

    override fun getLayoutId(): Int =R.layout.fragment_rm_calling
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val message = arguments?.getString(SocketInfo.KEY_MSG, getString(R.string.call_content))
        val isError = arguments?.getBoolean(SocketInfo.KEY_ERROR) ?: false

        val performerInfo: PerformerInfo =
            arguments?.get(BUNDLE_KEY.PERFORMER) as PerformerInfo
        binding.apply {
            tvName.text = getString(R.string.call_connection_message, performerInfo.name)
            ivAvatar.loadImage(performerInfo.imageUrl, R.drawable.ic_no_image)
            Glide.with(this@RMCallConnectionDialog).asGif().load(R.drawable.ic_rm_call_loading)
                .into(binding.ivMotion)
            ivEndCall.setOnClickListener {
                if (!isDoubleClick) {
                    callingCancelListener?.callingCancel(isError)
                    dismiss()
                }
            }
        }
        setMessage(message, isError)
    }

    fun setMessage(message: String?, isError: Boolean? = false) {
        message?.let {
            binding.tvNote.text = it
            if (isError == true) {
                binding.tvNote.setTextColor(resources.getColor(R.color.color_ff288b, null))
            } else {
                binding.tvNote.setTextColor(resources.getColor(R.color.color_B2A9CC, null))
            }
        }
    }

    fun setCallingCancelListener(callback: CallingCancelListener) {
        callingCancelListener = callback
    }

    interface CallingCancelListener {
        fun callingCancel(isError: Boolean)
    }

    companion object {
        fun newInstance(
            performerInfo: PerformerInfo,
            message: String,
            isError: Boolean = false
        ) = RMCallConnectionDialog().apply {
            arguments = Bundle().also {
                it.putSerializable(BUNDLE_KEY.PERFORMER, performerInfo)
                it.putString(SocketInfo.KEY_MSG, message)
                it.putBoolean(SocketInfo.KEY_ERROR, isError)
            }
        }
    }
}