package com.emberfocus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    @ColumnInfo(name = "color_key") val colorKey: String,
    @ColumnInfo(name = "created_at_epoch_day") val createdAtEpochDay: Long,
)

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["habit_id", "epoch_day"], unique = true)],
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "habit_id") val habitId: Long,
    @ColumnInfo(name = "epoch_day") val epochDay: Long,
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "started_at_epoch_ms") val startedAtEpochMs: Long,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    val completed: Boolean,
    @ColumnInfo(name = "preset_name") val presetName: String,
)
