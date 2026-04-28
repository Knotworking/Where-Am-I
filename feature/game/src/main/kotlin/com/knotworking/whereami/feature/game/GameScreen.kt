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
import com.knotworking.whereami.core.ui.theme.WhereAmITheme
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
        onAction = viewModel::onAction,
        uiState = uiState
    )
}

@Composable
fun GameScreen(
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onAction: (GameAction) -> Unit,
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
                onRetry = { onAction(GameAction.StartNewGame) }
            )

            uiState.isGameOver -> GameOverView(
                totalScore = uiState.totalScore,
                onRestart = { onAction(GameAction.StartNewGame) },
                onLeaderboardClick = onLeaderboardClick
            )

            else -> RoundView(
                uiState = uiState,
                onAction = onAction,
                onSettingsClick = onSettingsClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    WhereAmITheme {
        GameScreen(
            onSettingsClick = {},
            onLeaderboardClick = {},
            onAction = {},
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
