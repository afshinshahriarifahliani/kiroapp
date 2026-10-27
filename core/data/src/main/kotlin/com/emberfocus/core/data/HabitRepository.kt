package com.emberfocus.core.data

import com.emberfocus.core.data.mapper.toDomain
import com.emberfocus.core.data.mapper.toEntity
import com.emberfocus.core.database.dao.HabitDao
import com.emberfocus.core.database.entity.HabitCompletionEntity
import com.emberfocus.core.model.Habit
import com.emberfocus.core.model.HabitWithProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface HabitRepository {
    fun observeHabits(todayEpochDay: Long): Flow<List<HabitWithProgress>>
    suspend fun addHabit(name: String, emoji: String, colorKey: String, todayEpochDay: Long)
    suspend fun deleteHabit(habitId: Long)
    suspend fun toggleCompletion(habitId: Long, epochDay: Long, currentlyCompleted: Boolean)
}

class DefaultHabitRepository(
    private val dao: HabitDao,
) : HabitRepository {

    override fun observeHabits(todayEpochDay: Long): Flow<List<HabitWithProgress>> =
        combine(dao.observeHabits(), dao.observeCompletions()) { habits, completions ->
            val byHabit = completions.groupBy { it.habitId }
            habits.map { habit ->
                val days = byHabit[habit.id].orEmpty().map { it.epochDay }.toSortedSet()
                HabitWithProgress(
                    habit = habit.toDomain(),
                    completedToday = days.contains(todayEpochDay),
                    currentStreak = StreakCalculator.currentStreak(days, todayEpochDay),
                    bestStreak = StreakCalculator.bestStreak(days),
                    last7Days = (6 downTo 0).map { offset -> days.contains(todayEpochDay - offset) },
                )
            }
        }

    override suspend fun addHabit(name: String, emoji: String, colorKey: String, todayEpochDay: Long) {
        dao.upsertHabit(
            Habit(
                name = name,
                emoji = emoji,
                colorKey = colorKey,
                createdAtEpochDay = todayEpochDay,
            ).toEntity(),
        )
    }

    override suspend fun deleteHabit(habitId: Long) {
        dao.deleteCompletionsForHabit(habitId)
        dao.deleteHabitById(habitId)
    }

    override suspend fun toggleCompletion(habitId: Long, epochDay: Long, currentlyCompleted: Boolean) {
        if (currentlyCompleted) {
            dao.removeCompletion(habitId, epochDay)
        } else {
            dao.insertCompletion(HabitCompletionEntity(habitId = habitId, epochDay = epochDay))
        }
    }
}
