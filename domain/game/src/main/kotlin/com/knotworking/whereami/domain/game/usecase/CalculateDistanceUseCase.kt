package com.knotworking.whereami.domain.game.usecase

import javax.inject.Inject
import kotlin.math.*

class CalculateDistanceUseCase @Inject constructor() {
    /**
     * Calculates the geodesic distance between two points on Earth using the Haversine formula.
     * Returns the distance in meters.
     */
    operator fun invoke(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
