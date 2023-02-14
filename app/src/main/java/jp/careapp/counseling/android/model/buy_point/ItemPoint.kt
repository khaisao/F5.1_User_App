package jp.careapp.counseling.android.model.buy_point

data class ItemPoint(
    val itemId: String,
    var isFirstBuy: Boolean = false,
    val pointCount: String,
    val costFirst: String,
    val money: String,
    val pointToNumber: Int = 0,
    val moneyToNumber: Int = 0,
    val promote: Int = 0,
)
