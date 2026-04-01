package com.knotworking.whereami.feature.game.leaderboard

import com.knotworking.whereami.domain.game.model.HighScore

data class LeaderboardUiState(
    val scores: List<HighScore> = emptyList()
)
