package com.knotworking.whereami.feature.game

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class LeaderboardScreenRobot(private val rule: SemanticsNodeInteractionsProvider) {

    fun assertEmpty() {
        rule.onNodeWithText("No scores yet").assertIsDisplayed()
    }

    fun assertScore(score: Int) {
        rule.onNodeWithText(score.toString()).assertIsDisplayed()
    }

    fun clickClear() {
        // "Clear" icon has contentDescription=null by design; button is identified by its text label.
        rule.onNodeWithText("Clear").performClick()
    }
}
