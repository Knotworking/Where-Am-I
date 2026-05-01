package com.knotworking.whereami.feature.game

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardAction
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardScreen
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardUiState

class LeaderboardScreenRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>
) {

    fun setContent(
        uiState: LeaderboardUiState,
        onBack: () -> Unit = {},
        onAction: (LeaderboardAction) -> Unit = {}
    ) = apply {
        composeTestRule.setContent {
            LeaderboardScreen(uiState = uiState, onBack = onBack, onAction = onAction)
        }
    }

    fun assertEmpty() {
        val emptyText = composeTestRule.activity.getString(R.string.leaderboard_empty)
        composeTestRule.onNodeWithText(emptyText).assertIsDisplayed()
    }

    fun assertScore(score: Int) {
        composeTestRule.onNodeWithText(score.toString()).assertIsDisplayed()
    }

    fun clickClear() {
        val clearString = composeTestRule.activity.getString(R.string.leaderboard_clear)
        composeTestRule.onNodeWithText(clearString).performClick()
    }
}
