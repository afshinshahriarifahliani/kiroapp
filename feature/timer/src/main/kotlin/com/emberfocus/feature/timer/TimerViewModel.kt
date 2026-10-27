package com.emberfocus.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.data.SessionRepository
import com.emberfocus.core.model.Soundscape
import com.emberfocus.feature.timer.audio.SoundscapeController
import com.emberfocus.core.model.TimerPhase
import com.emberfocus.core.model.TimerPreset
import com.emberfocus.core.model.TimerStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the Pomodoro countdown. Uses a coroutine ticking every second; the countdown is anchored
 * to wall-clock time so it stays accurate across recomposition and brief backgrounding.
 */
class TimerViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val sessionRepository: SessionRepository,
    private val soundscapeController: SoundscapeController,
) : ViewModel() {

    private val _state = MutableStateFlow(TimerUiState())
    val state: StateFlow<TimerUiState> = _state.asStateFlow()

    private var tickJob: Job? = null
    private var deadlineMs: Long = 0L
    private var sessionStartMs: Long = 0L

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferences.collect { prefs ->
                val preset = TimerPreset.defaults.firstOrNull { it.id == prefs.selectedPresetId }
                    ?: TimerPreset.Classic
                _state.update { current ->
                    val applyPreset = current.status == TimerStatus.IDLE
                    current.copy(
                        preset = if (applyPreset) preset else current.preset,
                        remainingSeconds = if (applyPreset) preset.focusMinutes * 60 else current.remainingSeconds,
                        totalSeconds = if (applyPreset) preset.focusMinutes * 60 else current.totalSeconds,
                        soundscape = prefs.selectedSoundscape,
                        soundEnabled = prefs.soundEnabled,
                        isPro = prefs.isPro,
                    )
                }
                // Keep live playback in sync with preference changes while running.
                val s = _state.value
                if (s.status == TimerStatus.RUNNING) {
                    soundscapeController.apply(s.soundscape, s.soundEnabled, s.isPro)
                }
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            TimerEvent.StartPause -> toggleStartPause()
            TimerEvent.Reset -> reset()
            TimerEvent.Skip -> advancePhase(completed = false)
            is TimerEvent.SelectPreset -> selectPreset(event.preset)
            is TimerEvent.SelectSoundscape -> selectSoundscape(event.soundscape)
        }
    }

    private fun toggleStartPause() {
        when (_state.value.status) {
            TimerStatus.RUNNING -> pause()
            else -> start()
        }
    }

    private fun start() {
        val s = _state.value
        if (s.status == TimerStatus.IDLE || s.status == TimerStatus.FINISHED) {
            sessionStartMs = System.currentTimeMillis()
        }
        deadlineMs = System.currentTimeMillis() + s.remainingSeconds * 1000L
        _state.update { it.copy(status = TimerStatus.RUNNING) }
        soundscapeController.apply(s.soundscape, s.soundEnabled, s.isPro)
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (true) {
                val remaining = ((deadlineMs - System.currentTimeMillis()) / 1000L)
                    .toInt().coerceAtLeast(0)
                _state.update { it.copy(remainingSeconds = remaining) }
                if (remaining <= 0) {
                    onPhaseComplete()
                    break
                }
                delay(250)
            }
        }
    }

    private fun pause() {
        tickJob?.cancel()
        soundscapeController.stop()
        _state.update { it.copy(status = TimerStatus.PAUSED) }
    }

    private fun reset() {
        tickJob?.cancel()
        soundscapeController.stop()
        _state.update {
            val total = secondsFor(it.preset, it.phase)
            it.copy(status = TimerStatus.IDLE, remainingSeconds = total, totalSeconds = total)
        }
    }

    private fun onPhaseComplete() {
        val s = _state.value
        if (s.phase == TimerPhase.FOCUS) {
            viewModelScope.launch {
                sessionRepository.recordSession(
                    startedAtEpochMs = sessionStartMs,
                    durationMinutes = s.preset.focusMinutes,
                    completed = true,
                    presetName = s.preset.name,
                )
            }
        }
        advancePhase(completed = true)
    }

    private fun advancePhase(completed: Boolean) {
        tickJob?.cancel()
        soundscapeController.stop()
        _state.update { current ->
            val wasFocus = current.phase == TimerPhase.FOCUS
            val completedSessions =
                if (wasFocus) current.completedFocusSessions + 1 else current.completedFocusSessions
            val nextPhase = when {
                !wasFocus -> TimerPhase.FOCUS
                completedSessions % current.preset.sessionsBeforeLongBreak == 0 -> TimerPhase.LONG_BREAK
                else -> TimerPhase.SHORT_BREAK
            }
            val total = secondsFor(current.preset, nextPhase)
            current.copy(
                phase = nextPhase,
                status = TimerStatus.IDLE,
                remainingSeconds = total,
                totalSeconds = total,
                completedFocusSessions = completedSessions,
            )
        }
    }

    private fun selectPreset(preset: TimerPreset) {
        tickJob?.cancel()
        _state.update {
            val total = preset.focusMinutes * 60
            it.copy(
                preset = preset,
                phase = TimerPhase.FOCUS,
                status = TimerStatus.IDLE,
                remainingSeconds = total,
                totalSeconds = total,
            )
        }
        viewModelScope.launch { preferencesRepository.setSelectedPreset(preset.id) }
    }

    private fun selectSoundscape(soundscape: Soundscape) {
        _state.update { it.copy(soundscape = soundscape) }
        val s = _state.value
        if (s.status == TimerStatus.RUNNING) {
            soundscapeController.apply(soundscape, s.soundEnabled, s.isPro)
        }
        viewModelScope.launch { preferencesRepository.setSoundscape(soundscape) }
    }

    private fun secondsFor(preset: TimerPreset, phase: TimerPhase): Int = when (phase) {
        TimerPhase.FOCUS -> preset.focusMinutes * 60
        TimerPhase.SHORT_BREAK -> preset.shortBreakMinutes * 60
        TimerPhase.LONG_BREAK -> preset.longBreakMinutes * 60
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
        soundscapeController.stop()
    }
}

sealed interface TimerEvent {
    data object StartPause : TimerEvent
    data object Reset : TimerEvent
    data object Skip : TimerEvent
    data class SelectPreset(val preset: TimerPreset) : TimerEvent
    data class SelectSoundscape(val soundscape: Soundscape) : TimerEvent
}
