package com.emberfocus.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.emberfocus.core.datastore.PreferencesDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Thin wrapper around Play Billing. Exposes Pro-entitlement state as a flow, queries product
 * details for the paywall, and launches the purchase flow. Entitlement is cached in DataStore so
 * the UI reflects Pro instantly and offline.
 *
 * No backend is required: Play verifies purchases on-device for a solo app, and we acknowledge
 * them here. For stronger anti-piracy you could later add server-side receipt validation.
 */
class BillingManager(
    private val context: Context,
    private val preferences: PreferencesDataSource,
    private val scope: CoroutineScope,
) {
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _products = MutableStateFlow<List<ProductInfo>>(emptyList())
    val products: StateFlow<List<ProductInfo>> = _products.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val productDetailsCache = mutableMapOf<String, ProductDetails>()

    private val purchasesListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { purchases.forEach { handlePurchase(it) } }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesListener)
        .enablePendingPurchases(
            com.android.billingclient.api.PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    fun start() {
        if (billingClient.isReady) return
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _ready.value = true
                    scope.launch {
                        queryProducts()
                        refreshEntitlements()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                _ready.value = false
            }
        })
    }

    suspend fun refreshEntitlements() {
        if (!billingClient.isReady) return
        var pro = false
        val inApp = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )
        inApp.purchasesList.forEach { purchase ->
            if (purchase.isProEntitling()) {
                pro = true
                handlePurchase(purchase)
            }
        }
        val subs = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        )
        subs.purchasesList.forEach { purchase ->
            if (purchase.isProEntitling()) {
                pro = true
                handlePurchase(purchase)
            }
        }
        setPro(pro)
    }

    private suspend fun queryProducts() {
        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BillingProducts.inAppProducts.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                },
            )
            .build()
        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BillingProducts.subscriptionProducts.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                },
            )
            .build()

        val collected = mutableListOf<ProductInfo>()

        val inAppResult = billingClient.queryProductDetails(inAppParams)
        inAppResult.productDetailsList?.forEach { details ->
            productDetailsCache[details.productId] = details
            val price = details.oneTimePurchaseOfferDetails?.formattedPrice.orEmpty()
            collected += ProductInfo(details.productId, details.name, price, isSubscription = false)
        }

        val subResult = billingClient.queryProductDetails(subParams)
        subResult.productDetailsList?.forEach { details ->
            productDetailsCache[details.productId] = details
            val price = details.subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice
                .orEmpty()
            collected += ProductInfo(details.productId, details.name, price, isSubscription = true)
        }

        _products.value = collected
    }

    fun launchPurchase(activity: Activity, productId: String) {
        val details = productDetailsCache[productId] ?: return
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .apply {
                details.subscriptionOfferDetails?.firstOrNull()?.let {
                    setOfferToken(it.offerToken)
                }
            }
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params)
        }
        if (purchase.isProEntitling()) setPro(true)
    }

    private suspend fun setPro(value: Boolean) {
        _isPro.value = value
        preferences.setPro(value)
    }

    private fun Purchase.isProEntitling(): Boolean =
        products.any { it == BillingProducts.PRO_LIFETIME || it == BillingProducts.PRO_SUBSCRIPTION }

    fun dispose() {
        if (billingClient.isReady) billingClient.endConnection()
    }
}
