package com.emberfocus.core.data.mapper

import com.emberfocus.core.database.entity.FocusSessionEntity
import com.emberfocus.core.database.entity.HabitEntity
import com.emberfocus.core.model.FocusSession
import com.emberfocus.core.model.Habit

fun HabitEntity.toDomain() = Habit(
    id = id,
    name = name,
    emoji = emoji,
    colorKey = colorKey,
    createdAtEpochDay = createdAtEpochDay,
)

fun Habit.toEntity() = HabitEntity(
    id = id,
    name = name,
    emoji = emoji,
    colorKey = colorKey,
    createdAtEpochDay = createdAtEpochDay,
)

fun FocusSessionEntity.toDomain() = FocusSession(
    id = id,
    startedAtEpochMs = startedAtEpochMs,
    durationMinutes = durationMinutes,
    completed = completed,
    presetName = presetName,
)

fun FocusSession.toEntity() = FocusSessionEntity(
    id = id,
    startedAtEpochMs = startedAtEpochMs,
    durationMinutes = durationMinutes,
    completed = completed,
    presetName = presetName,
)
