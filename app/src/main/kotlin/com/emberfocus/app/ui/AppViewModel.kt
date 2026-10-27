package com.emberfocus.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.billing.BillingManager
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val loading: Boolean = true,
)

class AppViewModel(
    preferencesRepository: PreferencesRepository,
    private val billingManager: BillingManager,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> =
        preferencesRepository.userPreferences
            .map { AppUiState(themeMode = it.themeMode, loading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AppUiState(),
            )

    init {
        // Connect to Play Billing on launch so entitlement state is fresh everywhere.
        billingManager.start()
        viewModelScope.launch { billingManager.refreshEntitlements() }
    }
}
