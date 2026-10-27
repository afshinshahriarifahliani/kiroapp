package com.emberfocus.core.data

import com.emberfocus.core.datastore.PreferencesDataSource
import com.emberfocus.core.model.Soundscape
import com.emberfocus.core.model.ThemeMode
import com.emberfocus.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setKeepScreenOn(value: Boolean)
    suspend fun setSoundEnabled(value: Boolean)
    suspend fun setNotificationsEnabled(value: Boolean)
    suspend fun setSoundscape(soundscape: Soundscape)
    suspend fun setSelectedPreset(id: Long)
    suspend fun setOnboardingComplete(value: Boolean)
}

class DefaultPreferencesRepository(
    private val source: PreferencesDataSource,
) : PreferencesRepository {
    override val userPreferences: Flow<UserPreferences> = source.userPreferences
    override suspend fun setThemeMode(mode: ThemeMode) = source.setThemeMode(mode)
    override suspend fun setKeepScreenOn(value: Boolean) = source.setKeepScreenOn(value)
    override suspend fun setSoundEnabled(value: Boolean) = source.setSoundEnabled(value)
    override suspend fun setNotificationsEnabled(value: Boolean) = source.setNotificationsEnabled(value)
    override suspend fun setSoundscape(soundscape: Soundscape) = source.setSoundscape(soundscape)
    override suspend fun setSelectedPreset(id: Long) = source.setSelectedPreset(id)
    override suspend fun setOnboardingComplete(value: Boolean) = source.setOnboardingComplete(value)
}
