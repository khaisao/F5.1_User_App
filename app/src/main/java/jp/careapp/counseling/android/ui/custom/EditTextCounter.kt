package jp.careapp.counseling.android.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import jp.careapp.counseling.databinding.CustomEditTextCounterBinding

class EditTextCounter : FrameLayout {

    private lateinit var binding: CustomEditTextCounterBinding
    private var mContext: Context

    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding =
            CustomEditTextCounterBinding.inflate(LayoutInflater.from(this.context), this, true)
    }

    fun setTextChangeListener(listener: (Int) -> Unit) {
        binding.edt.addTextChangedListener {
            binding.tvCounter.text = "${10 - (it?.count() ?: 0)}"
            listener.invoke(it?.count() ?: 0)
        }
    }

    fun getText() = binding.edt.text?.trim().toString()

    fun setText(text: String) {
        binding.edt.setText(text)
        binding.tvCounter.text = "${10 - (binding.edt.text?.count() ?: 0)}"
    }

    fun setHint(hint: String?) {
        binding.edt.hint = hint
    }

    fun setTextViewCountInvisible() {
        binding.tvCounter.visibility = View.GONE
    }
}