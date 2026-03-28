package com.knotworking.whereami.domain.game.usecase

import javax.inject.Inject
import kotlin.math.exp

private const val MAX_SCORE = 5000
private const val PERFECT_SCORE_THRESHOLD_METERS = 50.0
private const val DECAY_CONSTANT_METERS = 2000000.0 // 2000 km for a more generous scoring

class CalculateScoreUseCase @Inject constructor() {
    /**
     * Calculates a score between 0 and 5000 based on the distance in meters.
     */
    operator fun invoke(distanceMeters: Double): Int {
        if (distanceMeters < PERFECT_SCORE_THRESHOLD_METERS) return MAX_SCORE

        // Using an exponential decay formula: score = maxScore * e^(-distance / decayConstant)
        // A decay constant of 1,000,000 meters (1000 km) means at 1000 km, the score is ~1839.
        // At 10,000 km, the score is ~0.2 (basically 0).
        val score = MAX_SCORE.toDouble() * exp(-distanceMeters / DECAY_CONSTANT_METERS)
        return score.toInt().coerceIn(0, MAX_SCORE)
    }
}
