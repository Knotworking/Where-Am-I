package com.knotworking.whereami.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.knotworking.whereami.feature.settings.SettingsScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object SettingsGraph

@Serializable
data object SettingsRoute

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onNavigateToLeaderboard: () -> Unit,
) {
    navigation<SettingsGraph>(startDestination = SettingsRoute) {
        composable<SettingsRoute> {
            SettingsScreenRoot(
                onBackClick = { navController.popBackStack() },
                onLeaderboardClick = onNavigateToLeaderboard,
            )
        }
    }
}
