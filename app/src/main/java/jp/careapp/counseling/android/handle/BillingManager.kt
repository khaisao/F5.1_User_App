package jp.careapp.counseling.android.handle

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.model.buy_point.ItemPoint
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_1
import jp.careapp.counseling.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_2
import jp.careapp.counseling.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_3
import jp.careapp.counseling.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

/**
 * Carelear
 * Created by Nguyen Van Vuong on 3/29/21.
 */

val TAG = BillingManager::class.simpleName

class BillingManager @Inject constructor(
    @ApplicationContext context: Context,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) :
    PurchasesUpdatedListener {
    private var billingClient: BillingClient? = null
    private var statusBilling = ERROR

    companion object {
        const val LOADING = 3
        const val CANCEL = 2
        const val ERROR = 1
        const val SUCCESS = 0
    }

    init {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    private var requestConnect = 0
    private var skuDetailsList: List<SkuDetails>? = null

    private fun processPurchases(purchasesResult: Set<Purchase>) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            purchasesResult.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    validPurchases.add(purchase)
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Log.d(TAG, "Received a pending purchase of SKU: ${purchase.skus}")
                } else {
                    Log.d(TAG, "Received a status: ${purchase.purchaseState}")
                }
            }
            if (validPurchases.isNullOrEmpty()) {
                handleActionBilling?.statusBilling(CANCEL)
            }
            validPurchases.forEach {
                val retry = 0
                verifiTokenBiling(it, retry)
            }
            statusBilling = CANCEL
        }

    suspend fun verifiTokenBiling(purchase: Purchase, retry: Int) {
        if (retry > 3) {
            handleActionBilling?.statusBilling(ERROR)
            return
        }
        statusBilling = ERROR
        try {
            var jsonObject = JSONObject()
            jsonObject.put(BUNDLE_KEY.OWNER_CODE, Define.OWNER_CODE)
            jsonObject.put(BUNDLE_KEY.MEMBER_CODE, rxPreferences.getMemberCode())
            jsonObject.put(BUNDLE_KEY.APP_CODE, Define.APP_CODE)
            jsonObject.put(BUNDLE_KEY.RECEIPT, JSONObject(purchase.originalJson.toString()))
            jsonObject.put(BUNDLE_KEY.SIGNATURE, purchase.signature)
            var body: RequestBody =
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    jsonObject.toString()
                )
            val response = apiInterface.checkBuyPoit(
                Define.URL_CONFIRM_POINT,
                body
            )
            if (response.equals("OK")) {
                val params =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.consumeAsync(params) { billingResult, purchaseToken ->
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            handleActionBilling?.statusBilling(SUCCESS)
                            statusBilling = SUCCESS
                        }
                    }
                }
            } else {
                handleActionBilling?.statusBilling(ERROR)
            }
        } catch (e: Exception) {
            statusBilling = ERROR
            verifiTokenBiling(purchase, retry + 1)
        }
    }

    fun startConnectionBilling() {
        if (!(billingClient?.isReady ?: false)) {
            billingClient?.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        requestConnect = 0
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                handleActionBilling?.statusBilling(LOADING)
                                querySkuDetails()
                                queryPurchasesAsync()
                            }
                            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                                startConnectionBilling()
                            }
                            else -> {
                                handleActionBilling?.statusBilling(CANCEL)
                            }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        startConnectionBilling()
                        handleActionBilling?.statusBilling(CANCEL)
                    }
                }
            )
        } else {
            handleActionBilling?.statusBilling(LOADING)
            querySkuDetails()
            queryPurchasesAsync()
        }
    }

    fun querySkuDetails() {
        val skuList =
            listOf(
                 IN_APP_PURCHASE_KEY_1,
                 IN_APP_PURCHASE_KEY_2,
                 IN_APP_PURCHASE_KEY_3,
                 IN_APP_PURCHASE_KEY_4,
            )
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                this@BillingManager.skuDetailsList = skuDetailsList
            }
        }
    }

    /**
     * query Purchases don't veryfi
     */
    fun queryPurchasesAsync() {
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)
        result?.purchasesList?.apply {
            purchasesResult.addAll(this)
        }
        processPurchases(purchasesResult)
    }

    fun startBilling(itemPoint: ItemPoint, activity: Activity) {
        // run main thread
        if (!skuDetailsList.isNullOrEmpty()) {
            val skuDetail = skuDetailsList?.firstOrNull {
                it.sku == itemPoint.itemId
            }
            if (skuDetail == null) {
                handleActionBilling?.statusBilling(CANCEL)
            }
            skuDetail?.let {
                if (billingClient?.isFeatureSupported(BillingClient.FeatureType.IN_APP_ITEMS_ON_VR)?.responseCode == BillingClient.BillingResponseCode.OK) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(it)
                        .build()
                    val responseCode =
                        billingClient?.launchBillingFlow(activity, flowParams)?.responseCode
                } else {
                    handleActionBilling?.statusBilling(CANCEL)
                }
            }
        } else {
            startConnectionBilling()
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.d(TAG, billingResult.debugMessage)

        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // will handle server verification, consumables, and updating the local cache
                purchases?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                startConnectionBilling()
                handleActionBilling?.statusBilling(CANCEL)
            }
            else -> {
                handleActionBilling?.statusBilling(CANCEL)
            }
        }
    }

    public fun onDestroy() {
        if (billingClient?.isReady ?: false) {
            billingClient?.endConnection()
        }
    }

    var handleActionBilling: HandleActionBilling? = null
    fun setHandleAction(handleActionBilling: HandleActionBilling) {
        this.handleActionBilling = handleActionBilling
    }

    interface HandleActionBilling {
        fun statusBilling(status: Int)
    }
}
