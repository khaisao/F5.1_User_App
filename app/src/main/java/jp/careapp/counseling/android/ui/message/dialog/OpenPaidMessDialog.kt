package jp.careapp.counseling.android.ui.message.dialog

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseDialogFragment
import jp.careapp.core.utils.dialog.OnNegativeDialogListener
import jp.careapp.core.utils.dialog.OnPositiveDialogListener
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.databinding.LayoutOpenPaidMessBinding
import javax.inject.Inject

@AndroidEntryPoint
class OpenPaidMessDialog : BaseDialogFragment<LayoutOpenPaidMessBinding>() {

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    override fun getLayoutId(): Int = R.layout.layout_open_paid_mess

    private var onPositiveListener: OnPositiveDialogListener? = null
    private var onNegativeListener: OnNegativeDialogListener? = null
    private var stopPosition = 0

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.apply {
            val payCharCount = getInt(DIALOG_PAY_CHAR_COUNT)
            val payPoint = getInt(DIALOG_PAY_POINT)
            onPositiveListener =
                arguments?.getParcelable(DIALOG_ON_POSITIVE_LISTENER) as OnPositiveDialogListener?
            onNegativeListener =
                arguments?.getParcelable(DIALOG_ON_NEGATIVE_LISTENER) as OnNegativeDialogListener?

            val videoType = getVideoType(payCharCount)
            playVideoConfirm(videoType)

            binding.apply {
                tvTitle.text = getString(R.string.unlock_dialog_title, payPoint)
                tvConfirm.setOnClickListener {
                    if (!isDoubleClick) {
                        if (payPoint <= rxPreferences.getPoint()) {
                            ivSkip.isVisible = true
                            layoutConfirm.isVisible = false
                            playVideoOpening(videoType)
                        } else {
                            handleBuyPoint.buyPoint(childFragmentManager)
                        }
                    }
                }
                tvCancel.setOnClickListener {
                    if (!isDoubleClick) {
                        onNegativeListener?.onClick()
                        dismiss()
                    }
                }
                ivSkip.setOnClickListener {
                    if (!isDoubleClick) {
                        videoBackground.stopPlayback()
                        if (!binding.layoutConfirm.isVisible) {
                            onPositiveListener?.onClick()
                        }
                        dismiss()
                    }
                }
            }
        }

        dialog?.setOnKeyListener { _: DialogInterface, keyCode: Int, keyEvent: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                if (!binding.layoutConfirm.isVisible) {
                    onPositiveListener?.onClick()
                }
                dismiss()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun getVideoType(payCharCount: Int): VideoType {
        return when (payCharCount) {
            in 0..100 -> VideoType.Small
            in 101..200 -> VideoType.Medium
            else -> VideoType.Large
        }
    }

    private fun playVideoConfirm(type: VideoType) {
        binding.apply {
            val uri = when (type) {
                VideoType.Small -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.confirmation_small)
                VideoType.Medium -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.confirmation_medium)
                VideoType.Large -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.confirmation_large)
            }
            videoBackground.apply {
                setVideoURI(uri)
                setOnPreparedListener {
                    it?.isLooping = true
                }
                start()
            }
        }
    }

    private fun playVideoOpening(type: VideoType) {
        binding.apply {
            val uri = when (type) {
                VideoType.Small -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.opening_mall)
                VideoType.Medium -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.opening_medium)
                VideoType.Large -> Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.opening_large)
            }
            videoBackground.apply {
                setVideoURI(uri)
                setOnPreparedListener {
                    it?.isLooping = false
                }
                setOnCompletionListener {
                    if (!binding.layoutConfirm.isVisible) {
                        onPositiveListener?.onClick()
                    }
                    dismiss()
                }
                start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (stopPosition > 0) {
                videoBackground.seekTo(stopPosition)
                videoBackground.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            stopPosition = videoBackground.currentPosition
            videoBackground.pause()
        }
    }

    companion object {
        const val TAG = "OpenPaidMessDialog"
        private const val DIALOG_PAY_CHAR_COUNT = "DIALOG_PAY_CHAR_COUNT"
        private const val DIALOG_PAY_POINT = "DIALOG_PAY_POINT"
        private const val DIALOG_ON_POSITIVE_LISTENER = "DIALOG_ON_POSITIVE_LISTENER"
        private const val DIALOG_ON_NEGATIVE_LISTENER = "DIALOG_ON_NEGATIVE_LISTENER"

        fun newInstance(
            payCharCount: Int,
            payPoint: Int,
            onPositiveClick: OnPositiveDialogListener? = null,
            onNegativeClick: OnNegativeDialogListener? = null,
        ) = OpenPaidMessDialog().apply {
            arguments = Bundle().also {
                it.putInt(DIALOG_PAY_CHAR_COUNT, payCharCount)
                it.putInt(DIALOG_PAY_POINT, payPoint)
                it.putParcelable(DIALOG_ON_POSITIVE_LISTENER, onPositiveClick)
                it.putParcelable(DIALOG_ON_NEGATIVE_LISTENER, onNegativeClick)
            }
        }
    }

    sealed class VideoType {
        object Small : VideoType()
        object Medium : VideoType()
        object Large : VideoType()
    }
}