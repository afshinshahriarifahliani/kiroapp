package com.emberfocus.feature.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.billing.BillingManager
import com.emberfocus.core.billing.ProductInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaywallUiState(
    val products: List<ProductInfo> = emptyList(),
    val isPro: Boolean = false,
    val ready: Boolean = false,
)

class PaywallViewModel(
    private val billingManager: BillingManager,
) : ViewModel() {

    val state: StateFlow<PaywallUiState> =
        combine(billingManager.products, billingManager.isPro, billingManager.ready) { products, isPro, ready ->
            PaywallUiState(products = products, isPro = isPro, ready = ready)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PaywallUiState(),
        )

    init {
        billingManager.start()
    }

    fun purchase(activity: Activity, productId: String) {
        billingManager.launchPurchase(activity, productId)
    }

    fun restore() {
        viewModelScope.launch { billingManager.refreshEntitlements() }
    }
}
