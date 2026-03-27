package com.knotworking.whereami.domain.game.usecase

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class CalculateScoreUseCaseTest {

    private val calculateScoreUseCase = CalculateScoreUseCase()

    @Test
    fun `score for 0 distance is 5000`() {
        val score = calculateScoreUseCase(0.0)
        assertThat(score).isEqualTo(5000)
    }

    @Test
    fun `score for 10m is 5000`() {
        val score = calculateScoreUseCase(10.0)
        assertThat(score).isEqualTo(5000)
    }

    @Test
    fun `score for 1000km is around 3032`() {
        val targetScore = 3032
        val acceptedVariance = 3
        val score = calculateScoreUseCase(1000000.0)
        assertThat(score).isBetween(targetScore - acceptedVariance, targetScore + acceptedVariance)
    }

    @Test
    fun `score for very large distance is 0`() {
        val score = calculateScoreUseCase(20000000.0) // 20,000 km
        assertThat(score).isEqualTo(0)
    }
}
