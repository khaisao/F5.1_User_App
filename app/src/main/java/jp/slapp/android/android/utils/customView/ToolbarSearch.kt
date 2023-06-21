package jp.slapp.android.android.utils.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import jp.careapp.core.utils.DeviceUtil
import jp.slapp.android.R

class ToolbarSearch : Toolbar {
    lateinit var btnLeft: ImageView
    lateinit var ivSearch: ImageView
    lateinit var tvRight: TextView
    lateinit var viewStatusBar: View
    lateinit var etSearch: EditText

    private var srcLeft = -1
    private var srcSearch = -1
    private var stringRight: String? = ""
    private var hintSearch: String? = ""
    private var isTransStatusBar = false
    private var toolBarSearchListener: ToolBarSearchListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ToolBarSearch, 0, 0)
        srcLeft = a.getResourceId(R.styleable.ToolBarSearch_src_left_search, -1)
        srcSearch = a.getResourceId(R.styleable.ToolBarSearch_ic_search, -1)
        stringRight = a.getString(R.styleable.ToolBarSearch_tv_right_search)
        hintSearch = a.getString(R.styleable.ToolBarSearch_string_hint_search)
        isTransStatusBar = a.getBoolean(R.styleable.ToolBarSearch_is_show_status_bar, false)

        init()
    }

    private fun init() {
        setContentInsetsAbsolute(0, 0)
        View.inflate(context, R.layout.tool_bar_search, this)
        btnLeft = findViewById(R.id.btn_left)
        tvRight = findViewById(R.id.tvRight)
        etSearch = findViewById(R.id.et_input_search)
        ivSearch = findViewById(R.id.ic_search)
        viewStatusBar = findViewById(R.id.view_status_bar)

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
                toolBarSearchListener?.clickLeftBtn()
            }
        )

        tvRight.setOnClickListener(
            OnClickListener {
                toolBarSearchListener?.clickRightBtn()
            }
        )

        if (srcLeft != -1) {
            btnLeft.setImageResource(srcLeft)
        }

        if (srcSearch != -1) {
            ivSearch.setImageResource(srcSearch)
        }

        etSearch.hint = hintSearch
        tvRight.text = stringRight
    }

    fun setToolBarSearchListener(toolBarSearchListener: ToolBarSearchListener) {
        this.toolBarSearchListener = toolBarSearchListener
    }

    abstract class ToolBarSearchListener {
        open fun clickLeftBtn() {}
        open fun clickRightBtn() {}
    }
}
