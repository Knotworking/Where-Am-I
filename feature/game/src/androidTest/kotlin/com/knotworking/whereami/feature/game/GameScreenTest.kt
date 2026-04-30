package com.knotworking.whereami.feature.game

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val robot by lazy { GameScreenRobot(composeTestRule) }

    @Test
    fun loading_shows_progress() {
        robot.setContent(uiState = GameUiState(isLoading = true))
            .assertLoading()
    }

    @Test
    fun error_shows_retry_button() {
        robot.setContent(uiState = GameUiState(error = GameError.NetworkError))
            .assertRetryButton()
    }

    @Test
    fun error_retry_fires_StartNewGame() {
        var capturedAction: GameAction? = null
        robot.setContent(
            uiState = GameUiState(error = GameError.NetworkError),
            onAction = { capturedAction = it }
        ).clickRetry()
        assertThat(capturedAction).isEqualTo(GameAction.StartNewGame)
    }

    @Test
    fun game_over_shows_score() {
        robot.setContent(uiState = GameUiState(isGameOver = true, totalScore = 12450))
            .assertGameOver(12450)
    }

    @Test
    fun game_over_start_new_fires_action() {
        var capturedAction: GameAction? = null
        robot.setContent(
            uiState = GameUiState(isGameOver = true, totalScore = 0),
            onAction = { capturedAction = it }
        ).clickStartNew()
        assertThat(capturedAction).isEqualTo(GameAction.StartNewGame)
    }

    @Test
    fun round_active_shows_round_counter() {
        // Map composable is not asserted — Google Maps doesn't render meaningfully in emulator tests.
        robot.setContent(uiState = GameUiState(currentRound = 2))
            .assertRoundCounter(current = 2, total = 5)
    }
}
