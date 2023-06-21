package jp.slapp.android.android.handle

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.model.buy_point.ItemPoint
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.Define
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_1
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_2
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_3
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
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
    private var productDetailsList: List<ProductDetails>? = null

    private fun processPurchases(purchasesResult: Set<Purchase>) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            purchasesResult.forEach { purchase ->
                when (purchase.purchaseState) {
                    Purchase.PurchaseState.PURCHASED -> {
                        validPurchases.add(purchase)
                    }
                    Purchase.PurchaseState.PENDING -> {
                        Timber.d(
                            TAG,
                            "Received a pending purchase of Product: ${purchase.products}"
                        )
                    }
                    else -> {
                        Timber.d(TAG, "Received a status: ${purchase.purchaseState}")
                    }
                }
            }
            if (validPurchases.isNullOrEmpty()) {
                handleActionBilling?.statusBilling(CANCEL)
            }
            validPurchases.forEach {
                val retry = 0
                verifyTokenBilling(it, retry)
            }
            statusBilling = CANCEL
        }

    private suspend fun verifyTokenBilling(purchase: Purchase, retry: Int) {
        if (retry > 3) {
            handleActionBilling?.statusBilling(ERROR)
            return
        }
        statusBilling = ERROR
        try {
            val jsonObject = JSONObject()
            jsonObject.put(BUNDLE_KEY.OWNER_CODE, BuildConfig.OWNER_CODE)
            jsonObject.put(BUNDLE_KEY.MEMBER_CODE, rxPreferences.getMemberCode())
            jsonObject.put(BUNDLE_KEY.APP_CODE, Define.APP_CODE)
            jsonObject.put(BUNDLE_KEY.RECEIPT, JSONObject(purchase.originalJson))
            jsonObject.put(BUNDLE_KEY.SIGNATURE, purchase.signature)
            val body: RequestBody =
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    jsonObject.toString()
                )
            val response = apiInterface.checkBuyPoit(
                Define.URL_CONFIRM_POINT,
                body
            )
            if (response == "OK") {
                val params =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.consumeAsync(params) { billingResult, _ ->
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
            verifyTokenBilling(purchase, retry + 1)
        }
    }

    fun startConnectionBilling() {
        if (billingClient?.isReady != true) {
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
                createInAppProduct(IN_APP_PURCHASE_KEY_1),
                createInAppProduct(IN_APP_PURCHASE_KEY_2),
                createInAppProduct(IN_APP_PURCHASE_KEY_3),
                createInAppProduct(IN_APP_PURCHASE_KEY_4),
            )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(skuList)
            .build()
        billingClient?.queryProductDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                this@BillingManager.productDetailsList = skuDetailsList
            }
        }
    }

    private fun createInAppProduct(productId: String) =
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

    /**
     * query Purchases don't veryfi
     */
    fun queryPurchasesAsync() {
        val purchasesResult = HashSet<Purchase>()
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, listPurchase ->
            purchasesResult.addAll(listPurchase)
            processPurchases(purchasesResult)
        }
    }

    fun startBilling(itemPoint: ItemPoint, activity: Activity) {
        // run main thread
        if (!productDetailsList.isNullOrEmpty()) {
            val productDetail = productDetailsList?.firstOrNull {
                it.productId == itemPoint.itemId
            }
            if (productDetail == null) {
                handleActionBilling?.statusBilling(CANCEL)
            }
            productDetail?.let {
                if (billingClient?.isFeatureSupported(BillingClient.FeatureType.IN_APP_MESSAGING)?.responseCode == BillingClient.BillingResponseCode.OK) {
                    val offerToken = it.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(it)
                            .setOfferToken(offerToken)
                            .build()
                    )
                    val flowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()
                    billingClient?.launchBillingFlow(activity, flowParams)
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
        Timber.d(TAG, billingResult.debugMessage)

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

    fun onDestroy() {
        if (billingClient?.isReady == true) {
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
