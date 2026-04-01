package com.knotworking.whereami

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knotworking.whereami.feature.game.GameScreenRoot
import com.knotworking.whereami.feature.game.LeaderboardScreenRoot
import com.knotworking.whereami.feature.settings.SettingsScreenRoot

@Composable
fun WhereAmINavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "game") {
        composable("game") {
            GameScreenRoot(
                onSettingsClick = { navController.navigate("settings") },
                onLeaderboardClick = { navController.navigate("leaderboard") }
            )
        }
        composable("settings") {
            SettingsScreenRoot(
                onBackClick = { navController.popBackStack() },
                onLeaderboardClick = { navController.navigate("leaderboard") }
            )
        }
        composable("leaderboard") {
            LeaderboardScreenRoot(onBack = { navController.popBackStack() })
        }
    }
}
