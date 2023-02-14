package jp.careapp.counseling.android.handle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.careapp.core.utils.ConverterUtils.Companion.defaultValue
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.BuyPointBottomFragment
import jp.careapp.counseling.android.utils.Define
import javax.inject.Inject

class HandleBuyPoint @Inject constructor(
    @ApplicationContext val context: Context,
    private val appNavigation: AppNavigation,
    private val rxPreferences: RxPreferences,
) {
    fun buyPoint(
        fragmentManager: FragmentManager,
        bundle: Bundle? = null,
        handleBuyPoint: BuyPointBottomFragment.HandleBuyPoint? = null,
        isClickSendMessage: Boolean = false
    ) {
        if (isFullMode) {
            val webViewBundle = Bundle().apply {
                putString(Define.TITLE_WEB_VIEW, context.getString(R.string.buy_point))
            }
            val lastBuyLog = rxPreferences.getLastBuyLog()
            if (!isClickSendMessage || rxPreferences.getFirstBuyCredit() || lastBuyLog == null) {
                webViewBundle.putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
            } else {
                val creditPrices = rxPreferences.getCreditPrices()
                val credit =
                    if (creditPrices.last().priceWithoutTax.defaultValue() == lastBuyLog.price.defaultValue()) {
                        creditPrices.last()
                    } else {
                        creditPrices.firstOrNull {
                            it.priceWithoutTax.defaultValue() > lastBuyLog.price.defaultValue()
                        }
                    }
                if (credit == null) {
                    webViewBundle.putString(Define.URL_WEB_VIEW, Define.URL_BUY_POINT)
                } else {
                    val url = Define.URL_CREDIT_PURCHASE_CONFIRM +
                            "?point=${credit.getTotalPoint()}" +
                            "&&money=${credit.price}" +
                            "&&realPoint=${credit.buyPoint}" +
                            "&&price_without_tax=${credit.priceWithoutTax}"
                    webViewBundle.putString(Define.URL_WEB_VIEW, url)
                }
            }
            appNavigation.openScreenToWebview(webViewBundle)
        } else {
            BuyPointBottomFragment.showPointBottomSheet(
                fragmentManager,
                bundle,
                handleBuyPoint
            )
        }
    }

    private val isFullMode: Boolean
        get() = rxPreferences.isFullMode()
}