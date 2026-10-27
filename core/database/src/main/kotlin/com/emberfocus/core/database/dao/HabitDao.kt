package com.emberfocus.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.emberfocus.core.database.entity.HabitCompletionEntity
import com.emberfocus.core.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_completions")
    fun observeCompletions(): Flow<List<HabitCompletionEntity>>

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity): Long

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Long)

    @Query("DELETE FROM habit_completions WHERE habit_id = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habit_id = :habitId AND epoch_day = :epochDay")
    suspend fun removeCompletion(habitId: Long, epochDay: Long)
}
