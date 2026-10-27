package com.emberfocus.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.data.SessionRepository
import com.emberfocus.core.model.FocusSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DayBar(val label: String, val minutes: Int)

data class StatsUiState(
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val todayMinutes: Int = 0,
    val weekBars: List<DayBar> = emptyList(),
    val isPro: Boolean = false,
    val recent: List<FocusSession> = emptyList(),
    val hasMoreHistory: Boolean = false,
)

class StatsViewModel(
    sessionRepository: SessionRepository,
    preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val state: StateFlow<StatsUiState> =
        combine(
            sessionRepository.observeSessions(),
            preferencesRepository.userPreferences,
        ) { sessions, prefs ->
            buildState(sessions, prefs.isPro)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState(),
        )

    private fun buildState(sessions: List<FocusSession>, isPro: Boolean): StatsUiState {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val byDay = sessions.groupBy {
            Instant.ofEpochMilli(it.startedAtEpochMs).atZone(zone).toLocalDate()
        }
        val bars = (6 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            val minutes = byDay[day].orEmpty().sumOf { it.durationMinutes }
            DayBar(label = day.dayOfWeek.name.take(1), minutes = minutes)
        }
        return StatsUiState(
            totalSessions = sessions.size,
            totalMinutes = sessions.sumOf { it.durationMinutes },
            todayMinutes = byDay[today].orEmpty().sumOf { it.durationMinutes },
            weekBars = bars,
            isPro = isPro,
            recent = sessions.take(com.emberfocus.core.model.ProLimits.sessionHistoryLimit(isPro).coerceAtMost(50)),
            hasMoreHistory = !isPro && sessions.size > com.emberfocus.core.model.ProLimits.FREE_SESSION_HISTORY,
        )
    }
}
