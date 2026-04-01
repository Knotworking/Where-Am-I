package com.knotworking.whereami.domain.game.usecase

import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import javax.inject.Inject

class ClearHighScoresUseCase @Inject constructor(
    private val repository: HighScoreRepository
) {
    suspend operator fun invoke() = repository.clearAll()
}
