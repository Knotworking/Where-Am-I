package com.knotworking.whereami.feature.game

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { GameScreenRobot(composeTestRule) }

    @Test
    fun loading_shows_progress() {
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = {},
                uiState = GameUiState(isLoading = true)
            )
        }
        robot.assertLoading()
    }

    @Test
    fun error_shows_retry_button() {
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = {},
                uiState = GameUiState(error = GameError.NetworkError)
            )
        }
        robot.assertRetryButton()
    }

    @Test
    fun error_retry_fires_StartNewGame() {
        var capturedAction: GameAction? = null
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = { capturedAction = it },
                uiState = GameUiState(error = GameError.NetworkError)
            )
        }
        robot.clickRetry()
        assertThat(capturedAction).isEqualTo(GameAction.StartNewGame)
    }

    @Test
    fun game_over_shows_score() {
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = {},
                uiState = GameUiState(isGameOver = true, totalScore = 12450)
            )
        }
        robot.assertGameOver(12450)
    }

    @Test
    fun game_over_start_new_fires_action() {
        var capturedAction: GameAction? = null
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = { capturedAction = it },
                uiState = GameUiState(isGameOver = true, totalScore = 0)
            )
        }
        robot.clickStartNew()
        assertThat(capturedAction).isEqualTo(GameAction.StartNewGame)
    }

    @Test
    fun round_active_shows_round_counter() {
        // Map composable is not asserted — GoogleMap doesn't render meaningfully in emulator tests.
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = {},
                onLeaderboardClick = {},
                onAction = {},
                uiState = GameUiState(currentRound = 2)
            )
        }
        robot.assertRoundCounter(current = 2, total = 5)
    }
}
