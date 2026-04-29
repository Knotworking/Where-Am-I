package com.knotworking.whereami.feature.game

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardAction
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardScreen
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaderboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { LeaderboardScreenRobot(composeTestRule) }

    @Test
    fun empty_state_shows_copy() {
        composeTestRule.setContent {
            LeaderboardScreen(
                uiState = LeaderboardUiState(isLoading = false, scores = emptyList()),
                onBack = {},
                onAction = {}
            )
        }
        robot.assertEmpty()
    }

    @Test
    fun populated_shows_score() {
        composeTestRule.setContent {
            LeaderboardScreen(
                uiState = LeaderboardUiState(
                    isLoading = false,
                    scores = listOf(
                        HighScore(id = 1L, totalScore = 18500, timestamp = 1_700_000_000_000L)
                    )
                ),
                onBack = {},
                onAction = {}
            )
        }
        robot.assertScore(18500)
    }

    @Test
    fun clear_fires_ClearAll() {
        var capturedAction: LeaderboardAction? = null
        composeTestRule.setContent {
            LeaderboardScreen(
                uiState = LeaderboardUiState(isLoading = false, scores = emptyList()),
                onBack = {},
                onAction = { capturedAction = it }
            )
        }
        robot.clickClear()
        assertThat(capturedAction).isEqualTo(LeaderboardAction.ClearAll)
    }
}
