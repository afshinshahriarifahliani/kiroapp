package com.emberfocus.core.billing

/** Play Console product identifiers. Create these in the Play Console before release. */
object BillingProducts {
    /** One-time purchase that unlocks Pro forever. */
    const val PRO_LIFETIME = "emberfocus_pro_lifetime"

    /** Auto-renewing subscription (monthly / yearly base plans configured in Play Console). */
    const val PRO_SUBSCRIPTION = "emberfocus_pro_sub"

    val inAppProducts = listOf(PRO_LIFETIME)
    val subscriptionProducts = listOf(PRO_SUBSCRIPTION)
}

data class ProductInfo(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val isSubscription: Boolean,
)
