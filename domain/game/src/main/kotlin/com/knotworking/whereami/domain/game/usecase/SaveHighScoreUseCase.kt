package com.knotworking.whereami.domain.game.usecase

import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import javax.inject.Inject

class SaveHighScoreUseCase @Inject constructor(
    private val repository: HighScoreRepository
) {
    suspend operator fun invoke(totalScore: Int) = repository.save(totalScore)
}
