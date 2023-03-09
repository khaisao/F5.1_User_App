package jp.careapp.core.utils.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import jp.careapp.core.R
import jp.careapp.core.utils.Constants

class RMCommonAlertDialog constructor(context: Context) : Dialog(context) {

    lateinit var btnConfirm: Button
    lateinit var tvTitle: TextView
    lateinit var tvSubTitle: TextView
    lateinit var tvCancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_rm_alert_error)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.apply {
            attributes = lp
            setGravity(Gravity.CENTER_VERTICAL)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        btnConfirm = findViewById(R.id.btn_confirm)
        tvTitle = findViewById(R.id.tv_title)
        tvSubTitle = findViewById(R.id.tv_subTitle)
        tvCancel = findViewById(R.id.tv_cancel)
        setCanceledOnTouchOutside(false)
        setOnPositivePressed {
            this.dismiss()
        }
        setOnNegativePressed{
            this.dismiss()
        }
    }

    fun setOnPositivePressed(onPositivePressed: ((RMCommonAlertDialog) -> Unit)?): RMCommonAlertDialog {
        btnConfirm.setOnClickListener {
            onPositivePressed?.invoke(this)
        }
        return this
    }

    fun setOnNegativePressed(onNegativePressed: ((RMCommonAlertDialog) -> Unit)?): RMCommonAlertDialog {
        tvCancel.setOnClickListener {
            onNegativePressed?.invoke(this)
        }
        return this
    }

    fun setTextNegativeButton(@StringRes textId: Int): RMCommonAlertDialog {
        tvCancel.text = context.resources.getString(textId)
        tvCancel.visibility = View.VISIBLE
        return this
    }

    fun setTextPositiveButton(@StringRes textId: Int): RMCommonAlertDialog {
        btnConfirm.text = context.resources.getString(textId)
        return this
    }

    fun setDialogTitle(res: Int): RMCommonAlertDialog {
        tvTitle.text = context.resources.getString(res)
        tvTitle.isVisible = true
        return this
    }

    fun setDialogTitle(title: String): RMCommonAlertDialog {
        tvTitle.text = title
        tvTitle.isVisible = true
        return this
    }

    fun setDialogTitleWithString(title: String): RMCommonAlertDialog {
        tvTitle.text = title
        tvTitle.isVisible = true
        return this
    }

    override fun setTitle(res: Int) {
        tvTitle.text = context.resources.getString(res)
        tvTitle.isVisible = true
    }

    fun setContent(vararg res: Int): RMCommonAlertDialog {
        var content = ""
        for (i in res.indices) {
            content += context.resources.getString(res[i])
            if (i < res.size - 1) content += "\n"
        }
        tvSubTitle.text = content
        tvSubTitle.visibility = View.VISIBLE
        return this
    }

    fun setContent(vararg contents: String): RMCommonAlertDialog {
        var content = ""
        for (i in contents.indices) {
            content += contents[i]
            if (i < contents.size - 1) content += "\n"
        }
        tvSubTitle.text = content
        tvSubTitle.visibility = View.VISIBLE
        return this
    }

    fun showDialog(): RMCommonAlertDialog {
        if (!isShowing)
            super.show()
        return this
    }

    fun setCancelableDialog(flag: Boolean): RMCommonAlertDialog {
        if (isShowing) {
            super.setCancelable(flag)
        }
        return this
    }

    var typeDialog = Constants.TYPE_DEFAULT

    companion object {
        var instance: RMCommonAlertDialog? = null
        fun getInstanceCommonAlertdialog(
            context: Context,
            type: Int = Constants.TYPE_DEFAULT
        ): RMCommonAlertDialog {
            if (instance != null && instance?.isShowing == true) {
                if (((instance?.typeDialog ?: Constants.TYPE_DEFAULT == Constants.TYPE_DEFAULT) || (type != instance?.typeDialog ?: Constants.TYPE_DEFAULT))) {
                    instance?.dismiss()
                } else {
                    return instance!!
                }
            } else {
                instance = RMCommonAlertDialog(context)
            }
            instance?.typeDialog = type
            return instance!!
        }
    }

    override fun dismiss() {
        val window = window ?: return
        val decor = window.decorView
        if (decor.parent != null) {
            super.dismiss()
            typeDialog = Constants.TYPE_DEFAULT
        }
    }
}
