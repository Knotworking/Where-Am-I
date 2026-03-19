package com.knotworking.whereami.domain.game.usecase

import org.junit.Assert.*
import org.junit.Test

class CalculateScoreUseCaseTest {

    private val calculateScoreUseCase = CalculateScoreUseCase()

    @Test
    fun `score for 0 distance is 5000`() {
        val score = calculateScoreUseCase(0.0)
        assertEquals(5000, score)
    }

    @Test
    fun `score for 10m is 5000`() {
        val score = calculateScoreUseCase(10.0)
        assertEquals(5000, score)
    }

    @Test
    fun `score for 1000km is around 3032`() {
        val score = calculateScoreUseCase(1000000.0)
        assertTrue("Expected around 3032 but was $score", Math.abs(3032 - score) <= 5)
    }

    @Test
    fun `score for very large distance is 0`() {
        val score = calculateScoreUseCase(20000000.0) // 20,000 km
        assertEquals(0, score)
    }
}
