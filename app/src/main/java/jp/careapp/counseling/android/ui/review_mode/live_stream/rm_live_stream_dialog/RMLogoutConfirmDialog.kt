package jp.careapp.counseling.android.ui.review_mode.live_stream.rm_live_stream_dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import jp.careapp.core.utils.Constants
import jp.careapp.counseling.R

class RMLogoutConfirmDialog constructor(context: Context) : Dialog(context) {

    lateinit var tvTitle: TextView
    lateinit var tvCancel: TextView
    lateinit var btnConfirm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_logout_confirm_dialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.apply {
            attributes = lp
            setGravity(Gravity.CENTER_VERTICAL)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        btnConfirm = findViewById(R.id.tv_confirm_log_out)
        tvCancel = findViewById(R.id.tv_cancel_log_out)
        setCanceledOnTouchOutside(false)
        setOnPositivePressed {
            this.dismiss()
        }
        setOnNegativePressed {
            this.dismiss()
        }
    }

    fun setOnPositivePressed(onPositivePressed: ((RMLogoutConfirmDialog) -> Unit)?): RMLogoutConfirmDialog {
        btnConfirm.setOnClickListener {
            onPositivePressed?.invoke(this)
        }
        return this
    }

    fun setOnNegativePressed(onNegativePressed: ((RMLogoutConfirmDialog) -> Unit)?): RMLogoutConfirmDialog {
        tvCancel.setOnClickListener {
            onNegativePressed?.invoke(this)
        }
        return this
    }

    fun showDialog(): RMLogoutConfirmDialog {
        if (!isShowing)
            super.show()
        return this
    }

    fun setCancelableDialog(flag: Boolean): RMLogoutConfirmDialog {
        if (isShowing) {
            super.setCancelable(flag)
        }
        return this
    }

    var typeDialog = Constants.TYPE_DEFAULT

    override fun dismiss() {
        val window = window ?: return
        val decor = window.decorView
        if (decor.parent != null) {
            super.dismiss()
            typeDialog = Constants.TYPE_DEFAULT
        }
    }
}
