package jp.careapp.counseling.android.utils.customView

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R

class ToolbarPoint : Toolbar {
    lateinit var btnLeft: ImageView
    lateinit var btnRight: ImageView
    lateinit var ivCall: ImageView
    lateinit var tvTitle: TextView
    lateinit var tvTitleBack: TextView
    lateinit var viewStatusBar: View
    lateinit var tvPoint: TextView

    private var srcLeft = -1
    private var srcRight = -1
    private var stringTitle: String? = ""
    private var stringTitleBack: String? = ""
    private var isTransStatusBar = false
    private var isShowPoint = false
    private var isShowCall = false
    private var toolBarPointListener: ToolBarPointListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ToolBarPoint, 0, 0)
        srcLeft = a.getResourceId(R.styleable.ToolBarPoint_src_left_p, -1)
        srcRight = a.getResourceId(R.styleable.ToolBarPoint_src_right_p, -1)
        stringTitle = a.getString(R.styleable.ToolBarPoint_string_title_p)
        stringTitleBack = a.getString(R.styleable.ToolBarPoint_string_title_back_p)
        isTransStatusBar = a.getBoolean(R.styleable.ToolBarPoint_is_show_status_bar_p, false)
        isShowPoint = a.getBoolean(R.styleable.ToolBarPoint_is_show_point, false)
        isShowCall = a.getBoolean(R.styleable.ToolBarPoint_is_show_call, false)
        if (TextUtils.isEmpty(stringTitle)) {
            stringTitle = resources.getString(
                a.getResourceId(
                    R.styleable.ToolBarPoint_string_title_p,
                    R.string.text_empty
                )
            )
        }
        init()
    }

    private fun init() {
        setContentInsetsAbsolute(0, 0)
        View.inflate(context, R.layout.tool_bar_point, this)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btnRight)
        tvTitle = findViewById(R.id.tv_title)
        tvTitleBack = findViewById(R.id.title_back_tv)
        viewStatusBar = findViewById(R.id.view_status_bar)
        tvPoint = findViewById(R.id.tvPoint)
        ivCall = findViewById(R.id.ivCall)
        if (isTransStatusBar) {
            viewStatusBar.visibility = View.VISIBLE
            val layoutParams =
                viewStatusBar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = DeviceUtil.getStatusBarHeight(context)
        } else {
            viewStatusBar.visibility = View.GONE
        }
        btnLeft.setOnClickListener(
            OnClickListener {
                toolBarPointListener?.clickLeftBtn()
            }
        )
        btnRight.setOnClickListener(
            OnClickListener {
                toolBarPointListener?.clickRightBtn()
            }
        )

        tvPoint.setOnClickListener {
            toolBarPointListener?.clickPointBtn()
        }
        tvTitleBack.setOnClickListener {
            toolBarPointListener?.clickTitleBack()
        }

        if (isShowPoint) {
            tvPoint.visibility = View.VISIBLE
        }
        if (srcLeft != -1) {
            btnLeft.setImageResource(srcLeft)
        }
        if (srcRight != -1) {
            btnRight.isVisible = true
            btnRight.setImageResource(srcRight)
        } else {
            btnRight.isVisible = false
        }
        tvTitle.isVisible = stringTitle?.isNotEmpty() == true
        tvTitle.text = stringTitle
        tvTitleBack.text = stringTitleBack
        ivCall.isVisible = isShowCall
    }

    fun setPoint(point: String) {
        tvPoint.setText(point)
    }

    fun setTitleBack(title: String) {
        tvTitleBack.setText(title)
    }

    fun setToolBarPointListener(toolBarPointListener: ToolBarPointListener) {
        this.toolBarPointListener = toolBarPointListener
    }

    fun setVisibleButtonCall(isVisible: Boolean) {
        ivCall.isVisible = isVisible
    }

    fun setEnableButtonCall(isEnable: Boolean) {
        ivCall.isEnabled = isEnable
        ivCall.alpha = if (isEnable) 1.0F else 0.5F
    }

    fun setOnClickButtonCall(callback: () -> Unit) {
        ivCall.setOnClickListener {
            callback.invoke()
        }
    }

    abstract class ToolBarPointListener {
        open fun clickLeftBtn() {}
        open fun clickRightBtn() {}
        open fun clickPointBtn() {}
        open fun clickTitleBack() {}
    }
}
