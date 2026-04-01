package com.knotworking.whereami.domain.game.usecase

import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHighScoresUseCase @Inject constructor(
    private val repository: HighScoreRepository
) {
    operator fun invoke(): Flow<List<HighScore>> = repository.getTopScores()
}
