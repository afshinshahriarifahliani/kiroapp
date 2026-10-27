package com.emberfocus.core.data

/** Pure functions for computing habit streaks from a set of completed epoch-days. */
object StreakCalculator {

    /**
     * The current streak counts consecutive completed days ending at [today], or at yesterday if
     * today is not yet done (so the streak is preserved until the day fully ends).
     */
    fun currentStreak(completedDays: Set<Long>, today: Long): Int {
        if (completedDays.isEmpty()) return 0
        var anchor = when {
            completedDays.contains(today) -> today
            completedDays.contains(today - 1) -> today - 1
            else -> return 0
        }
        var streak = 0
        while (completedDays.contains(anchor)) {
            streak++
            anchor--
        }
        return streak
    }

    /** The longest run of consecutive completed days ever recorded. */
    fun bestStreak(completedDays: Set<Long>): Int {
        if (completedDays.isEmpty()) return 0
        val sorted = completedDays.sorted()
        var best = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i] == sorted[i - 1] + 1) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }
}
