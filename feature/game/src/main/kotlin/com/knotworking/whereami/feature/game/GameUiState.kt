package com.knotworking.whereami.feature.game

import com.knotworking.whereami.domain.game.model.Guess
import com.knotworking.whereami.domain.photo.model.Photo

data class GameUiState(
    val isLoading: Boolean = false,
    val isPhotoLoading: Boolean = false,
    val currentPhoto: Photo? = null,
    val currentRound: Int = 1,
    val totalScore: Int = 0,
    val guesses: List<Guess> = emptyList(),
    val lastGuess: Guess? = null,
    val isGameOver: Boolean = false,
    val error: GameError? = null
)
