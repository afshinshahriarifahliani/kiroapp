package com.emberfocus.core.model

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val keepScreenOn: Boolean = true,
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val selectedSoundscape: Soundscape = Soundscape.NONE,
    val selectedPresetId: Long = TimerPreset.Classic.id,
    val onboardingComplete: Boolean = false,
    val isPro: Boolean = false,
)
