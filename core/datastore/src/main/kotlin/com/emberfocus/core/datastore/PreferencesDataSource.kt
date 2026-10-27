package com.emberfocus.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.emberfocus.core.model.Soundscape
import com.emberfocus.core.model.ThemeMode
import com.emberfocus.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Reads and writes [UserPreferences] backed by a Preferences DataStore. */
class PreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            keepScreenOn = prefs[Keys.KEEP_SCREEN_ON] ?: true,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            selectedSoundscape = prefs[Keys.SOUNDSCAPE]?.let {
                runCatching { Soundscape.valueOf(it) }.getOrNull()
            } ?: Soundscape.NONE,
            selectedPresetId = prefs[Keys.PRESET_ID] ?: 1L,
            onboardingComplete = prefs[Keys.ONBOARDING] ?: false,
            isPro = prefs[Keys.IS_PRO] ?: false,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) = edit { it[Keys.THEME] = mode.name }
    suspend fun setKeepScreenOn(value: Boolean) = edit { it[Keys.KEEP_SCREEN_ON] = value }
    suspend fun setSoundEnabled(value: Boolean) = edit { it[Keys.SOUND_ENABLED] = value }
    suspend fun setNotificationsEnabled(value: Boolean) = edit { it[Keys.NOTIFICATIONS_ENABLED] = value }
    suspend fun setSoundscape(soundscape: Soundscape) = edit { it[Keys.SOUNDSCAPE] = soundscape.name }
    suspend fun setSelectedPreset(id: Long) = edit { it[Keys.PRESET_ID] = id }
    suspend fun setOnboardingComplete(value: Boolean) = edit { it[Keys.ONBOARDING] = value }
    suspend fun setPro(value: Boolean) = edit { it[Keys.IS_PRO] = value }

    private suspend inline fun edit(crossinline block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        dataStore.edit { block(it) }
    }

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SOUNDSCAPE = stringPreferencesKey("soundscape")
        val PRESET_ID = longPreferencesKey("preset_id")
        val ONBOARDING = booleanPreferencesKey("onboarding_complete")
        val IS_PRO = booleanPreferencesKey("is_pro")
    }
}
