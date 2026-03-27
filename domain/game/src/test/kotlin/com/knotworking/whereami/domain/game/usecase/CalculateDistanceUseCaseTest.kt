package com.knotworking.whereami.domain.game.usecase

import assertk.assertThat
import assertk.assertions.isCloseTo
import org.junit.jupiter.api.Test

class CalculateDistanceUseCaseTest {

    private val calculateDistanceUseCase = CalculateDistanceUseCase()

    @Test
    fun `calculate distance between London and Paris`() {
        // London: 51.5074 N, 0.1278 W
        // Paris: 48.8566 N, 2.3522 E
        val distance = calculateDistanceUseCase(51.5074, -0.1278, 48.8566, 2.3522)

        // Expected distance is approximately 343.5 km using Haversine
        assertThat(distance).isCloseTo(343556.0, 10.0)
    }

    @Test
    fun `calculate distance between same points`() {
        val distance = calculateDistanceUseCase(10.0, 10.0, 10.0, 10.0)

        assertThat(distance).isCloseTo(0.0, 0.001)
    }
}
