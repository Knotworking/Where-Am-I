package com.knotworking.whereami.feature.game

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardAction
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaderboardScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val robot by lazy { LeaderboardScreenRobot(composeTestRule) }

    @Test
    fun empty_state_shows_copy() {
        robot.setContent(uiState = LeaderboardUiState(isLoading = false, scores = emptyList()))
            .assertEmpty()
    }

    @Test
    fun populated_shows_score() {
        val score = 18500
        val uiState = LeaderboardUiState(
            isLoading = false,
            scores = listOf(
                HighScore(
                    id = 1L,
                    totalScore = score,
                    timestamp = 1_700_000_000_000L
                )
            )
        )

        robot.setContent(uiState = uiState)
            .assertScore(score)
    }

    @Test
    fun clear_fires_ClearAll() {
        var capturedAction: LeaderboardAction? = null
        robot.setContent(
            uiState = LeaderboardUiState(isLoading = false, scores = emptyList()),
            onAction = { capturedAction = it }
        ).clickClear()
        assertThat(capturedAction).isEqualTo(LeaderboardAction.ClearAll)
    }
}
