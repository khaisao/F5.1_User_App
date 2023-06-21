package jp.slapp.android.android.utils.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import jp.careapp.core.utils.Constants
import jp.slapp.android.R

class GenderSelectView : RelativeLayout {

    lateinit var rlChooseGender: RelativeLayout
    lateinit var tvTitleGender: TextView
    lateinit var rdgGender: RadioGroup
    lateinit var rbMale: RadioButton
    lateinit var rbFemale: RadioButton
    lateinit var rbOther: RadioButton
    lateinit var viewBg: View
    private var onChooseGender: ChooseGender? = null
    private var currentSelect = -1
    private var currentNameSelect: String? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val view = inflate(context, R.layout.layout_choose_gender, null)
        addView(view)

        rlChooseGender = findViewById(R.id.rlChooseGender)
        tvTitleGender = findViewById(R.id.tv_title_gender)
        rdgGender = findViewById(R.id.rg_gender)
        rbMale = findViewById(R.id.rb_male)
        rbFemale = findViewById(R.id.rb_female)
        rbOther = findViewById(R.id.rb_other)
        viewBg = findViewById(R.id.view_bg)
        handleChooseGender()
    }

    private fun handleChooseGender() {
        rdgGender.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_male -> {
                    currentSelect = Constants.Gender.MALE.index
                    currentNameSelect = Constants.Gender.MALE.label
                }
                R.id.rb_female -> {
                    currentSelect = Constants.Gender.FEMALE.index
                    currentNameSelect = Constants.Gender.FEMALE.label
                }
                else -> {
                    currentSelect = Constants.Gender.OTHER.index
                    currentNameSelect = Constants.Gender.OTHER.label
                }
            }
        }

        viewBg.setOnClickListener {
            onChooseGender.let { it!!.choose(currentSelect, currentNameSelect) }
        }
    }
    fun setDefaultSelected(index: Int = 1) {
        when (index) {
            Constants.Gender.MALE.index -> {
                currentNameSelect = Constants.Gender.MALE.label
                currentSelect = Constants.Gender.MALE.index
                rbMale.isChecked = true
            }
            Constants.Gender.FEMALE.index -> {
                currentNameSelect = Constants.Gender.FEMALE.label
                currentSelect = Constants.Gender.FEMALE.index
                rbFemale.isChecked = true
            }
            else -> {
                currentSelect = Constants.Gender.OTHER.index
                currentNameSelect = Constants.Gender.OTHER.label
                rbOther.isChecked = true
            }
        }
    }
    fun setOnChooseGender(onChooseGender: ChooseGender) {
        this.onChooseGender = onChooseGender
    }

    interface ChooseGender {
        fun choose(pos: Int, title: String?)
    }
}
