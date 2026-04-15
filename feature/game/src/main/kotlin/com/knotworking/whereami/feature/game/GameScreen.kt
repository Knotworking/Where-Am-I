package com.knotworking.whereami.feature.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knotworking.whereami.core.ui.asString
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.feature.game.ui.ErrorView
import com.knotworking.whereami.feature.game.ui.GameOverView
import com.knotworking.whereami.feature.game.ui.LoadingView
import com.knotworking.whereami.feature.game.ui.RoundView

@Composable
fun GameScreenRoot(
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreen(
        onSettingsClick = onSettingsClick,
        onLeaderboardClick = onLeaderboardClick,
        onStartNewGame = viewModel::startNewGame,
        onNextRound = viewModel::nextRound,
        onSubmitGuess = viewModel::submitGuess,
        uiState = uiState
    )
}

@Composable
fun GameScreen(
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onStartNewGame: () -> Unit,
    onNextRound: () -> Unit,
    onSubmitGuess: (Double, Double) -> Unit,
    uiState: GameUiState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> LoadingView()
            uiState.error != null -> ErrorView(
                message = uiState.error.toUiText().asString(),
                onRetry = onStartNewGame
            )

            uiState.isGameOver -> GameOverView(
                totalScore = uiState.totalScore,
                onRestart = onStartNewGame,
                onLeaderboardClick = onLeaderboardClick
            )

            else -> RoundView(
                uiState = uiState,
                onSubmitGuess = onSubmitGuess,
                onNextRound = onNextRound,
                onSettingsClick = onSettingsClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MaterialTheme {
        GameScreen(
            onSettingsClick = {},
            onLeaderboardClick = {},
            onStartNewGame = {},
            onNextRound = {},
            onSubmitGuess = { _, _ -> },
            uiState = GameUiState(
                currentRound = 3,
                totalScore = 8500,
                currentPhoto = Photo(
                    id = "1",
                    title = "Dublin",
                    latitude = 53.3498,
                    longitude = -6.2603,
                    urlM = null
                )
            )
        )
    }
}
