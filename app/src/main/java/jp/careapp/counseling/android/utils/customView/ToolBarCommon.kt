package jp.careapp.counseling.android.utils.customView

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R

class ToolBarCommon : Toolbar {
    lateinit var toolbar: ConstraintLayout
    lateinit var btnRight: TextView
    lateinit var rootLayout: View
    lateinit var btnLeft: ImageView
    lateinit var ivRight: ImageView
    lateinit var tvRight: TextView
    lateinit var tvTitle: TextView
    lateinit var viewStatusBar: View
    lateinit var lineBottom: View
    private var srcLeft = -1
    private var srcButtonRight = -1
    private var srcBtnRight: String? = ""
    private var srcRight: String? = ""
    private var stringTitle: String? = ""

    private var isTransStatusBar = false
    private var isRotationSrcLeft = false
    private var hiddenLineBottom = false
    private var onToolBarClickListener: OnToolBarClickListener? = null

    private var fontTitle = 0
    private var colorTitle = 0
    private var colorTvRight = 0
    private var bgToolBar = 0

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
        srcBtnRight = a.getString(R.styleable.ToolBarCommon_src_btn_right)
        stringTitle = a.getString(R.styleable.ToolBarCommon_string_title)
        fontTitle = a.getResourceId(R.styleable.ToolBarCommon_font_title, R.font.hiragino_sans_w6)
        colorTitle = a.getResourceId(R.styleable.ToolBarCommon_color_title, R.color.color_E5E5E5)
        colorTvRight = a.getResourceId(R.styleable.ToolBarCommon_color_tv_right, R.color.white)
        bgToolBar =
            a.getResourceId(R.styleable.ToolBarCommon_bg_toolbar, R.color.color_background_common)
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
        btnRight = findViewById(R.id.btnRight)
        rootLayout = findViewById(R.id.toolbar)
        btnLeft = findViewById(R.id.btn_left)
        tvRight = findViewById(R.id.tvRight)
        ivRight = findViewById(R.id.ivRight)
        tvTitle = findViewById(R.id.tv_title)
        toolbar = findViewById(R.id.toolbar)
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
        btnLeft.setOnClickListener {
            onToolBarClickListener?.onClickLeft()
        }
        tvRight.setOnClickListener {
            onToolBarClickListener?.onClickRight()
        }
        btnRight.setOnClickListener {
            onToolBarClickListener?.onClickRight()
        }

        ivRight.setOnClickListener {
            onToolBarClickListener?.onClickRight()
        }

        if (isRotationSrcLeft) {
            btnLeft.rotation = 45F
        }
        setSrcLeft(srcLeft)
        setDrawableRight(srcButtonRight)
        setSrcForTvRight(srcRight)
        setTvTitle(stringTitle)
        setSrcForBtnRight(srcBtnRight)
        setFontTitle(fontTitle)
        setColorTitle(colorTitle)
        setColorTvRight(colorTvRight)
        setBackgroundToolBar(bgToolBar)
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

    fun setSrcForTvRight(src: String?) {
        if (src != null) {
            tvRight.visibility = View.VISIBLE
            tvRight.text = src
        } else {
            tvRight.visibility = View.INVISIBLE
        }
    }

    private fun setSrcForBtnRight(src: String?) {
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

    fun setBackgroundBtnRight(backgroundRes: Int) {
        btnRight.setBackgroundResource(backgroundRes)
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
        tvRight.isEnabled = enable
    }

    fun setImageRightEnable(enable: Boolean) {
        ivRight.isEnabled = enable
    }

    fun setRootLayoutBackgroundColor(color: Int) {
        rootLayout.setBackgroundColor(color)
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

    fun enableBtnRight(isEnabled: Boolean) {
        btnRight.isEnabled = isEnabled
    }

    private fun setFontTitle(@FontRes resource: Int) {
        tvTitle.typeface = ResourcesCompat.getFont(context!!, resource)
    }

    private fun setColorTitle(@ColorRes color: Int) {
        tvTitle.setTextColor(resources.getColor(color))
    }

    private fun setColorTvRight(@ColorRes color: Int) {
        tvRight.setTextColor(resources.getColor(color))
    }

    private fun setBackgroundToolBar(@ColorRes color: Int) {
        toolbar.setBackgroundColor(resources.getColor(color))
    }
}
