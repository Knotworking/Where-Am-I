package com.knotworking.whereami.data.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY totalScore DESC LIMIT 10")
    fun getTopScores(): Flow<List<HighScoreEntity>>

    @Insert
    suspend fun insert(entity: HighScoreEntity)

    @Query("DELETE FROM high_scores")
    suspend fun deleteAll()
}
