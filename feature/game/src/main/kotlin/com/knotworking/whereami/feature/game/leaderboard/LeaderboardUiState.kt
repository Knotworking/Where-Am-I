package com.knotworking.whereami.feature.game.leaderboard

import com.knotworking.whereami.domain.game.model.HighScore

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val scores: List<HighScore> = emptyList()
)
