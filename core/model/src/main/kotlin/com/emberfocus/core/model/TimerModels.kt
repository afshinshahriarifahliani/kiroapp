package com.emberfocus.core.model

/** A single focus session configuration. */
data class TimerPreset(
    val id: Long = 0,
    val name: String,
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val sessionsBeforeLongBreak: Int,
    val isPro: Boolean = false,
) {
    companion object {
        val Classic = TimerPreset(
            id = 1,
            name = "Classic",
            focusMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            sessionsBeforeLongBreak = 4,
        )
        val Deep = TimerPreset(
            id = 2,
            name = "Deep Work",
            focusMinutes = 50,
            shortBreakMinutes = 10,
            longBreakMinutes = 20,
            sessionsBeforeLongBreak = 3,
        )
        val Sprint = TimerPreset(
            id = 3,
            name = "Sprint",
            focusMinutes = 15,
            shortBreakMinutes = 3,
            longBreakMinutes = 10,
            sessionsBeforeLongBreak = 4,
        )

        val defaults = listOf(Classic, Deep, Sprint)
    }
}

enum class TimerPhase {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK;

    val isBreak: Boolean get() = this != FOCUS
}

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    FINISHED,
}

/** A completed focus session, used for stats. */
data class FocusSession(
    val id: Long = 0,
    val startedAtEpochMs: Long,
    val durationMinutes: Int,
    val completed: Boolean,
    val presetName: String,
)
