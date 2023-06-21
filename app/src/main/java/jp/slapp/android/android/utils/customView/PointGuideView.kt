package jp.slapp.android.android.utils.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import jp.slapp.android.R

class PointGuideView : ConstraintLayout {

    private var itemDrawable = -1
    private var itemTile: String? = ""
    private var itemDescription: String? = ""
    private var itemBronzeFee = 0
    private var itemSilverFee = 0
    private var itemGoldFee = 0
    private var itemPlatinumFee = 0
    private var isMinuteCalculate = false
    private var isExpend = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.PointGuide, 0, 0)
        itemDrawable = a.getResourceId(R.styleable.PointGuide_itemDrawable, -1)
        itemTile = a.getString(R.styleable.PointGuide_itemTitle)
        itemDescription = a.getString(R.styleable.PointGuide_itemDescription)
        itemBronzeFee = a.getInt(R.styleable.PointGuide_itemBronzeFee, 0)
        itemSilverFee = a.getInt(R.styleable.PointGuide_itemSilverFee, 0)
        itemGoldFee = a.getInt(R.styleable.PointGuide_itemGoldFee, 0)
        itemPlatinumFee = a.getInt(R.styleable.PointGuide_itemPlatinumFee, 0)
        isMinuteCalculate = a.getBoolean(R.styleable.PointGuide_isMinuteCalculate, false)
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.layout_point_guide, this)

        val ivItemIcon = findViewById<AppCompatImageView>(R.id.ivItemIcon)
        ivItemIcon.setBackgroundResource(itemDrawable)

        val tvItemTitle = findViewById<AppCompatTextView>(R.id.tvItemTitle)
        tvItemTitle.text = itemTile

        val clPointGuide = findViewById<ConstraintLayout>(R.id.clPointGuide)
        clPointGuide.visibility = if (isExpend) VISIBLE else GONE

        val ivNavigationItem = findViewById<AppCompatImageView>(R.id.ivNavigationItem)
        ivNavigationItem.setBackgroundResource(if (isExpend) R.drawable.ic_arrow_up_point_guide else R.drawable.ic_arrow_down_point_guide)

        val tvItemDescription = findViewById<AppCompatTextView>(R.id.tvItemDescription)
        tvItemDescription.text = itemDescription

        val tvItemBronzeFeeValue = findViewById<AppCompatTextView>(R.id.tvItemBronzeFeeValue)
        tvItemBronzeFeeValue.text = getPointFeeDisplay(itemBronzeFee)

        val tvItemSilverFeeValue = findViewById<AppCompatTextView>(R.id.tvItemSilverFeeValue)
        tvItemSilverFeeValue.text = getPointFeeDisplay(itemSilverFee)

        val tvItemGoldFeeValue = findViewById<AppCompatTextView>(R.id.tvItemGoldFeeValue)
        tvItemGoldFeeValue.text = getPointFeeDisplay(itemGoldFee)

        val tvItemPlatinumFeeValue = findViewById<AppCompatTextView>(R.id.tvItemPlatinumFeeValue)
        tvItemPlatinumFeeValue.text = getPointFeeDisplay(itemPlatinumFee)

        setOnClickListener {
            isExpend = !isExpend
            clPointGuide.visibility = if (isExpend) VISIBLE else GONE
            ivNavigationItem.setBackgroundResource(if (isExpend) R.drawable.ic_arrow_up_point_guide else R.drawable.ic_arrow_down_point_guide)
        }
    }

    private fun getPointFeeDisplay(pointCount: Int): String =
        if (isMinuteCalculate) resources.getString(R.string.point_unit_2, pointCount)
        else resources.getString(R.string.point_unit_1, pointCount)
}