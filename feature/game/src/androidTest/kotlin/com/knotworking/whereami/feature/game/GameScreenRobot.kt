package com.knotworking.whereami.feature.game

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class GameScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>
) {

    fun setContent(
        uiState: GameUiState,
        onSettingsClick: () -> Unit = {},
        onLeaderboardClick: () -> Unit = {},
        onAction: (GameAction) -> Unit = {}
    ) = apply {
        composeTestRule.setContent {
            GameScreen(
                onSettingsClick = onSettingsClick,
                onLeaderboardClick = onLeaderboardClick,
                onAction = onAction,
                uiState = uiState,
                // Google Maps requires an API key even in tests; bypass it with a no-op
                mapContent = { _, _, _ -> }
            )
        }
    }

    fun assertLoading() {
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun assertRetryButton() {
        val retryText = composeTestRule.activity.getString(R.string.error_try_again)
        composeTestRule.onNodeWithText(retryText).assertIsDisplayed()
    }

    fun clickRetry() {
        val retryText = composeTestRule.activity.getString(R.string.error_try_again)
        composeTestRule.onNodeWithText(retryText).performClick()
    }

    fun assertGameOver(score: Int) {
        composeTestRule.onNodeWithText(score.toString()).assertIsDisplayed()
    }

    fun clickStartNew() {
        val startNewText = composeTestRule.activity.getString(R.string.game_start_new)
        composeTestRule.onNodeWithText(startNewText).performClick()
    }

    fun assertRoundCounter(current: Int, total: Int) {
        val counterText = composeTestRule.activity.getString(R.string.game_round_counter, current, total)
        composeTestRule.onNodeWithText(counterText).assertIsDisplayed()
    }
}
