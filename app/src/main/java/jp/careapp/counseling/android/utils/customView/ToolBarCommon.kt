package jp.careapp.counseling.android.utils.customView

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R

class ToolBarCommon : Toolbar {
    lateinit var btnLeft: ImageView
    lateinit var ivRight: ImageView
    lateinit var btnRight: TextView
    lateinit var tvTitle: TextView
    lateinit var viewStatusBar: View
    lateinit var lineBottom: View
    private var srcLeft = -1
    private var srcButtonRight = -1
    private var srcRight: String? = ""
    private var stringTitle: String? = ""
    private var isTransStatusBar = false
    private var isRotationSrcLeft = false
    private var hiddenLineBottom = false
    private var onToolBarClickListener: OnToolBarClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ToolBarCommon, 0, 0)
        srcLeft = a.getResourceId(R.styleable.ToolBarCommon_src_left, -1)
        srcButtonRight = a.getResourceId(R.styleable.ToolBarCommon_iv_right, -1)
        srcRight = a.getString(R.styleable.ToolBarCommon_src_right)
        stringTitle = a.getString(R.styleable.ToolBarCommon_string_title)
        isTransStatusBar = a.getBoolean(R.styleable.ToolBarCommon_trans_status_bar, false)
        isRotationSrcLeft = a.getBoolean(R.styleable.ToolBarCommon_rotation_src_left, false)
        hiddenLineBottom = a.getBoolean(R.styleable.ToolBarCommon_hide_line_bottom, false)
        if (TextUtils.isEmpty(stringTitle)) {
            stringTitle = resources.getString(
                a.getResourceId(
                    R.styleable.ToolBarCommon_string_title,
                    R.string.text_empty
                )
            )
        }
        init()
    }

    private fun init() {
        setContentInsetsAbsolute(0, 0)
        View.inflate(context, R.layout.tool_bar_common, this)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.tvRight)
        ivRight = findViewById(R.id.ivRight)
        tvTitle = findViewById(R.id.tv_title)
        viewStatusBar = findViewById(R.id.view_status_bar)
        viewStatusBar = findViewById(R.id.view_status_bar)
        lineBottom = findViewById(R.id.line_bottom)
        if (isTransStatusBar) {
            viewStatusBar.visibility = View.VISIBLE
            val layoutParams =
                viewStatusBar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = DeviceUtil.getStatusBarHeight(context)
        } else {
            viewStatusBar.visibility = View.GONE
        }
        if (hiddenLineBottom) {
            lineBottom.visibility = View.GONE
        }
        btnLeft.setOnClickListener(
            OnClickListener {
                if (onToolBarClickListener != null) {
                    onToolBarClickListener!!.onClickLeft()
                }
            }
        )
        btnRight.setOnClickListener(
            OnClickListener {
                if (onToolBarClickListener != null) {
                    onToolBarClickListener!!.onClickRight()
                }
            }
        )

        ivRight.setOnClickListener {
            if (onToolBarClickListener != null) {
                onToolBarClickListener!!.onClickRight()
            }
        }

        if (isRotationSrcLeft) {
            btnLeft.rotation = 45F
        }
        setSrcLeft(srcLeft)
        setDrawableRight(srcButtonRight)
        setSrcRight(srcRight)
        setTvTitle(stringTitle)
    }

    fun setRotationSrcLeft(rotation: Float) {
        btnLeft.rotation = rotation
    }

    fun setSrcLeft(@DrawableRes src: Int) {
        if (src != -1) {
            btnLeft.visibility = View.VISIBLE
            btnLeft.setImageResource(src)
        } else {
            btnLeft.visibility = View.INVISIBLE
        }
    }

    fun setDrawableRight(@DrawableRes src: Int) {
        if (src != -1) {
            ivRight.visibility = View.VISIBLE
            ivRight.setImageResource(src)
        } else {
            ivRight.visibility = View.INVISIBLE
        }
    }

    fun setSrcRight(src: String?) {
        if (src != null) {
            btnRight.visibility = View.VISIBLE
            btnRight.text = src
        } else {
            btnRight.visibility = View.INVISIBLE
        }
    }

    fun setColorTvTitle(color: Int, activity: Activity) {
        tvTitle.setTextColor(resources.getColor(color, activity.theme))
    }

    fun setColorBtnRight(color: Int, activity: Activity) {
        btnRight.setTextColor(resources.getColor(color, activity.theme))
    }

    fun setTvTitle(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
        } else {
            tvTitle.text = ""
        }
    }

    fun setLeftEnable(enable: Boolean) {
        btnLeft.isEnabled = enable
    }

    fun setRightEnable(enable: Boolean) {
        btnRight.isEnabled = enable
    }

    fun setImageRightEnable(enable: Boolean) {
        ivRight.isEnabled = enable
    }

    fun setOnToolBarClickListener(onToolBarClickListener: OnToolBarClickListener?) {
        this.onToolBarClickListener = onToolBarClickListener
    }

    abstract class OnToolBarClickListener {
        open fun onClickLeft() {
        }

        open fun onClickRight() {
        }
    }
}
