package com.knotworking.whereami.domain.game.usecase

import javax.inject.Inject
import kotlin.math.exp

class CalculateScoreUseCase @Inject constructor() {
    /**
     * Calculates a score between 0 and 5000 based on the distance in meters.
     */
    operator fun invoke(distanceMeters: Double): Int {
        if (distanceMeters < 50.0) return 5000
        
        // Using an exponential decay formula: score = maxScore * e^(-distance / decayConstant)
        // A decay constant of 1,000,000 meters (1000 km) means at 1000 km, the score is ~1839.
        // At 10,000 km, the score is ~0.2 (basically 0).
        val maxScore = 5000.0
        val decayConstant = 2000000.0 // 2000 km for a more generous scoring
        
        val score = maxScore * exp(-distanceMeters / decayConstant)
        return score.toInt().coerceIn(0, 5000)
    }
}
