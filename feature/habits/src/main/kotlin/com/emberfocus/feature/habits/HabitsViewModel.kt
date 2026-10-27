package com.emberfocus.feature.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.data.HabitRepository
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.model.HabitWithProgress
import com.emberfocus.core.model.ProLimits
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitsUiState(
    val habits: List<HabitWithProgress> = emptyList(),
    val isPro: Boolean = false,
    val atFreeLimit: Boolean = false,
)

class HabitsViewModel(
    private val repository: HabitRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private fun today(): Long = LocalDate.now().toEpochDay()

    private val _today = MutableStateFlow(today())

    /** Emitted when a free user tries to exceed the habit limit, so the UI can open the paywall. */
    private val _limitReached = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val limitReached: SharedFlow<Unit> = _limitReached.asSharedFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HabitsUiState> =
        combine(
            _today.flatMapLatest { day -> repository.observeHabits(day) },
            preferencesRepository.userPreferences,
        ) { habits, prefs ->
            HabitsUiState(
                habits = habits,
                isPro = prefs.isPro,
                atFreeLimit = !prefs.isPro && habits.size >= ProLimits.FREE_HABIT_LIMIT,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HabitsUiState(),
        )

    fun addHabit(name: String, emoji: String, colorKey: String) {
        if (name.isBlank()) return
        val state = uiState.value
        if (!state.isPro && state.habits.size >= ProLimits.FREE_HABIT_LIMIT) {
            _limitReached.tryEmit(Unit)
            return
        }
        viewModelScope.launch {
            repository.addHabit(name.trim(), emoji, colorKey, today())
        }
    }

    /** True if tapping "add" should route to the paywall instead of the add sheet. */
    fun shouldUpsell(): Boolean {
        val state = uiState.value
        return !state.isPro && state.habits.size >= ProLimits.FREE_HABIT_LIMIT
    }

    fun toggle(habit: HabitWithProgress) {
        viewModelScope.launch {
            repository.toggleCompletion(habit.habit.id, today(), habit.completedToday)
        }
    }

    fun delete(habitId: Long) {
        viewModelScope.launch { repository.deleteHabit(habitId) }
    }
}
