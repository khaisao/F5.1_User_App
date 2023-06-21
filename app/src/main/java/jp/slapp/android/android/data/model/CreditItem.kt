package jp.slapp.android.android.data.model

import com.google.gson.annotations.SerializedName
import jp.careapp.core.utils.ConverterUtils.Companion.defaultValue

data class CreditItem(
    @SerializedName("price") val price: Int? = 0,
    @SerializedName("price_without_tax") val priceWithoutTax: Int? = 0,
    @SerializedName("buy_point") val buyPoint: Int? = 0,
    @SerializedName("bonus_point") val bonusPoint: Int? = 0,
    @SerializedName("info") val info: String? = ""
) {
    fun getTotalPoint(): Int {
        return buyPoint.defaultValue() + bonusPoint.defaultValue()
    }
}