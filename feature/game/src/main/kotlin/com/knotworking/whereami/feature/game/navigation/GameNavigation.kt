package com.knotworking.whereami.feature.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.knotworking.whereami.feature.game.GameScreenRoot
import com.knotworking.whereami.feature.game.leaderboard.LeaderboardScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object GameRoute

@Serializable
data object LeaderboardRoute

fun NavGraphBuilder.gameGraph(
    navController: NavController,
    onNavigateToSettings: () -> Unit,
) {
    navigation<GameRoute>(startDestination = GameRoute) {
        composable<GameRoute> {
            GameScreenRoot(
                onSettingsClick = onNavigateToSettings,
                onLeaderboardClick = { navController.navigate(LeaderboardRoute) },
            )
        }
        composable<LeaderboardRoute> {
            LeaderboardScreenRoot(onBack = { navController.popBackStack() })
        }
    }
}
