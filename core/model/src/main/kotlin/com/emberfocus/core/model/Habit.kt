package com.emberfocus.core.model

/** A daily habit the user wants to maintain a streak on. */
data class Habit(
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val colorKey: String,
    val createdAtEpochDay: Long,
)

/** A single completion record for a habit on a given day. */
data class HabitCompletion(
    val id: Long = 0,
    val habitId: Long,
    val epochDay: Long,
)

/** A habit with its computed streak info for display. */
data class HabitWithProgress(
    val habit: Habit,
    val completedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val last7Days: List<Boolean>,
)
