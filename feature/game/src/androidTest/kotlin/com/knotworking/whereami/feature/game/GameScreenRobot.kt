package com.knotworking.whereami.feature.game

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class GameScreenRobot(private val rule: SemanticsNodeInteractionsProvider) {

    fun assertLoading() {
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun assertRetryButton() {
        rule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    fun clickRetry() {
        rule.onNodeWithText("Try Again").performClick()
    }

    fun assertGameOver(score: Int) {
        rule.onNodeWithText(score.toString()).assertIsDisplayed()
    }

    fun clickStartNew() {
        rule.onNodeWithText("Start New Game").performClick()
    }

    fun assertRoundCounter(current: Int, total: Int) {
        rule.onNodeWithText("$current/$total").assertIsDisplayed()
    }
}
