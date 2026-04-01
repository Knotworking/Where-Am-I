package com.knotworking.whereami.data.game

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores")
data class HighScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalScore: Int,
    val timestamp: Long
)
