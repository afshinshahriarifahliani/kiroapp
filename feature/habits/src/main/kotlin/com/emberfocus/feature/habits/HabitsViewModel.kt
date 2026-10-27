package com.emberfocus.feature.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emberfocus.core.data.HabitRepository
import com.emberfocus.core.model.HabitWithProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitsUiState(
    val habits: List<HabitWithProgress> = emptyList(),
    val isPro: Boolean = false,
)

class HabitsViewModel(
    private val repository: HabitRepository,
) : ViewModel() {

    private fun today(): Long = LocalDate.now().toEpochDay()

    private val _today = MutableStateFlow(today())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val habits: StateFlow<List<HabitWithProgress>> =
        _today.flatMapLatest { day -> repository.observeHabits(day) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun addHabit(name: String, emoji: String, colorKey: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(name.trim(), emoji, colorKey, today())
        }
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
