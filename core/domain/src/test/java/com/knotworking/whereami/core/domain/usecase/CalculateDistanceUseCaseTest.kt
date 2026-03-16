package com.knotworking.whereami.core.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateDistanceUseCaseTest {

    private val calculateDistanceUseCase = CalculateDistanceUseCase()

    @Test
    fun `calculate distance between London and Paris`() {
        // London: 51.5074 N, 0.1278 W
        // Paris: 48.8566 N, 2.3522 E
        val distance = calculateDistanceUseCase(51.5074, -0.1278, 48.8566, 2.3522)
        
        // Expected distance is approximately 343.5 km using Haversine
        assertEquals(343556.0, distance, 10.0)
    }

    @Test
    fun `calculate distance between same points`() {
        val distance = calculateDistanceUseCase(10.0, 10.0, 10.0, 10.0)
        assertEquals(0.0, distance, 0.001)
    }
}
