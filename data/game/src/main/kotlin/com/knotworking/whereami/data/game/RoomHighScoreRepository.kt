package com.knotworking.whereami.data.game

import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomHighScoreRepository @Inject constructor(
    private val dao: HighScoreDao
) : HighScoreRepository {

    override fun getTopScores(): Flow<List<HighScore>> =
        dao.getTopScores().map { entities ->
            entities.map { HighScore(id = it.id, totalScore = it.totalScore, timestamp = it.timestamp) }
        }

    override suspend fun save(totalScore: Int) {
        dao.insert(HighScoreEntity(totalScore = totalScore, timestamp = System.currentTimeMillis()))
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}
