package com.knotworking.whereami.domain.game.repository

import com.knotworking.whereami.domain.game.model.HighScore
import kotlinx.coroutines.flow.Flow

interface HighScoreRepository {
    fun getTopScores(): Flow<List<HighScore>>
    suspend fun save(totalScore: Int)
    suspend fun clearAll()
}
