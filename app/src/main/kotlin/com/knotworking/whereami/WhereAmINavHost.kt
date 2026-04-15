package com.knotworking.whereami

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.knotworking.whereami.feature.game.navigation.GameGraph
import com.knotworking.whereami.feature.game.navigation.LeaderboardRoute
import com.knotworking.whereami.feature.game.navigation.gameGraph
import com.knotworking.whereami.feature.settings.navigation.SettingsGraph
import com.knotworking.whereami.feature.settings.navigation.settingsGraph

@Composable
fun WhereAmINavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = GameGraph) {
        gameGraph(
            navController = navController,
            onNavigateToSettings = { navController.navigate(SettingsGraph) },
        )
        settingsGraph(
            navController = navController,
            onNavigateToLeaderboard = { navController.navigate(LeaderboardRoute) },
        )
    }
}
