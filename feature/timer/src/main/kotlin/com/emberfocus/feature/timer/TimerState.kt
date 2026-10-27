package com.emberfocus.feature.timer

import com.emberfocus.core.model.Soundscape
import com.emberfocus.core.model.TimerPhase
import com.emberfocus.core.model.TimerPreset
import com.emberfocus.core.model.TimerStatus

data class TimerUiState(
    val preset: TimerPreset = TimerPreset.Classic,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val status: TimerStatus = TimerStatus.IDLE,
    val remainingSeconds: Int = TimerPreset.Classic.focusMinutes * 60,
    val totalSeconds: Int = TimerPreset.Classic.focusMinutes * 60,
    val completedFocusSessions: Int = 0,
    val availablePresets: List<TimerPreset> = TimerPreset.defaults,
    val soundscape: Soundscape = Soundscape.NONE,
    val soundEnabled: Boolean = true,
    val isPro: Boolean = false,
) {
    val progress: Float
        get() = if (totalSeconds == 0) 0f else 1f - (remainingSeconds.toFloat() / totalSeconds)

    val isRunning: Boolean get() = status == TimerStatus.RUNNING
}
