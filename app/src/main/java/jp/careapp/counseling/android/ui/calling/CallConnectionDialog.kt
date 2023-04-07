package jp.careapp.counseling.android.ui.calling

import android.os.Bundle
import com.bumptech.glide.Glide
import jp.careapp.core.base.BaseDialogFragment
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.PERFORMER
import jp.careapp.counseling.android.utils.SocketInfo.KEY_ERROR
import jp.careapp.counseling.android.utils.SocketInfo.KEY_MSG
import jp.careapp.counseling.databinding.FragmentCallingBinding

class CallConnectionDialog : BaseDialogFragment<FragmentCallingBinding>() {

    private var callingCancelListener: CallingCancelListener? = null
    override fun getLayoutId(): Int = R.layout.fragment_calling

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val performerInfo: ConsultantResponse =
            arguments?.get(PERFORMER) as ConsultantResponse
        binding.apply {
            tvName.text = getString(R.string.call_connection_message, performerInfo.name)
            if (performerInfo.imageUrl?.isNotEmpty() == true) {
                ivAvatar.loadImage(performerInfo.imageUrl, false)
            } else {
                ivAvatar.loadImage(R.drawable.ic_avatar_default, false)
            }
            Glide.with(this@CallConnectionDialog).asGif().load(R.drawable.ic_call_loading)
                .into(binding.ivMotion)
            ivEndCall.setOnClickListener {
                if (!isDoubleClick) {
                    callingCancelListener?.callingCancel()
                    dismiss()
                }
            }
        }

        val message = arguments?.getString(KEY_MSG, getString(R.string.call_content))
        val isError = arguments?.getBoolean(KEY_ERROR)
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
        fun callingCancel()
    }

    companion object {
        fun newInstance(
            performerInfo: ConsultantResponse,
            message: String,
            isError: Boolean = false
        ) = CallConnectionDialog().apply {
            arguments = Bundle().also {
                it.putSerializable(PERFORMER, performerInfo)
                it.putString(KEY_MSG, message)
                it.putBoolean(KEY_ERROR, isError)
            }
        }
    }
}