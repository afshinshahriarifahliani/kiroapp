package com.emberfocus.core.model

/**
 * Single source of truth for what the free tier allows versus EmberFocus Pro. Keeping the limits
 * here means every feature enforces the same rules the paywall advertises.
 */
object ProLimits {
    /** Free users can track up to this many habits; Pro is unlimited. */
    const val FREE_HABIT_LIMIT = 3

    /** Free users see this many recent focus sessions in Stats; Pro sees full history. */
    const val FREE_SESSION_HISTORY = 5

    fun habitLimit(isPro: Boolean): Int = if (isPro) Int.MAX_VALUE else FREE_HABIT_LIMIT
    fun sessionHistoryLimit(isPro: Boolean): Int = if (isPro) Int.MAX_VALUE else FREE_SESSION_HISTORY
}
