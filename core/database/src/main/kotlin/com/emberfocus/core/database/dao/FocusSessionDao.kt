package com.emberfocus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emberfocus.core.database.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insert(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY started_at_epoch_ms DESC")
    fun observeSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE started_at_epoch_ms >= :sinceEpochMs ORDER BY started_at_epoch_ms DESC")
    fun observeSessionsSince(sinceEpochMs: Long): Flow<List<FocusSessionEntity>>
}
