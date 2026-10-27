package com.emberfocus.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.model.ThemeMode
import com.emberfocus.core.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val state: StateFlow<UserPreferences> =
        preferencesRepository.userPreferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    fun setThemeMode(mode: ThemeMode) = launch { preferencesRepository.setThemeMode(mode) }
    fun setKeepScreenOn(value: Boolean) = launch { preferencesRepository.setKeepScreenOn(value) }
    fun setSoundEnabled(value: Boolean) = launch { preferencesRepository.setSoundEnabled(value) }
    fun setNotifications(value: Boolean) = launch { preferencesRepository.setNotificationsEnabled(value) }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
