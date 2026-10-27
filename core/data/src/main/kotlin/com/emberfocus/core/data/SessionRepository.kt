package com.emberfocus.core.data

import com.emberfocus.core.data.mapper.toDomain
import com.emberfocus.core.database.dao.FocusSessionDao
import com.emberfocus.core.database.entity.FocusSessionEntity
import com.emberfocus.core.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SessionRepository {
    fun observeSessions(): Flow<List<FocusSession>>
    suspend fun recordSession(startedAtEpochMs: Long, durationMinutes: Int, completed: Boolean, presetName: String)
}

class DefaultSessionRepository(
    private val dao: FocusSessionDao,
) : SessionRepository {

    override fun observeSessions(): Flow<List<FocusSession>> =
        dao.observeSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun recordSession(
        startedAtEpochMs: Long,
        durationMinutes: Int,
        completed: Boolean,
        presetName: String,
    ) {
        dao.insert(
            FocusSessionEntity(
                startedAtEpochMs = startedAtEpochMs,
                durationMinutes = durationMinutes,
                completed = completed,
                presetName = presetName,
            ),
        )
    }
}
