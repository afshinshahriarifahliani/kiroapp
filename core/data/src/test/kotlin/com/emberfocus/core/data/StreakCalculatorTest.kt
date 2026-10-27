package com.emberfocus.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    @Test
    fun `current streak counts consecutive days ending today`() {
        val days = setOf(10L, 11L, 12L)
        assertEquals(3, StreakCalculator.currentStreak(days, today = 12L))
    }

    @Test
    fun `current streak preserved when today not yet done but yesterday was`() {
        val days = setOf(10L, 11L)
        assertEquals(2, StreakCalculator.currentStreak(days, today = 12L))
    }

    @Test
    fun `current streak is zero when gap before today`() {
        val days = setOf(5L, 6L)
        assertEquals(0, StreakCalculator.currentStreak(days, today = 12L))
    }

    @Test
    fun `current streak is zero with no completions`() {
        assertEquals(0, StreakCalculator.currentStreak(emptySet(), today = 12L))
    }

    @Test
    fun `best streak finds longest run`() {
        val days = setOf(1L, 2L, 3L, 7L, 8L, 20L)
        assertEquals(3, StreakCalculator.bestStreak(days))
    }

    @Test
    fun `best streak handles single day`() {
        assertEquals(1, StreakCalculator.bestStreak(setOf(42L)))
    }
}
