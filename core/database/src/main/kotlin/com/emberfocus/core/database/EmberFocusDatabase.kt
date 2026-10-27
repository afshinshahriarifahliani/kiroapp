package com.emberfocus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emberfocus.core.database.dao.FocusSessionDao
import com.emberfocus.core.database.dao.HabitDao
import com.emberfocus.core.database.entity.FocusSessionEntity
import com.emberfocus.core.database.entity.HabitCompletionEntity
import com.emberfocus.core.database.entity.HabitEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        FocusSessionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class EmberFocusDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun focusSessionDao(): FocusSessionDao
}
