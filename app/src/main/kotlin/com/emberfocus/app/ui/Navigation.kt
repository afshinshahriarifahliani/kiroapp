package com.emberfocus.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emberfocus.feature.habits.HabitsScreen
import com.emberfocus.feature.paywall.PaywallScreen
import com.emberfocus.feature.settings.SettingsScreen
import com.emberfocus.feature.stats.StatsScreen
import com.emberfocus.feature.timer.TimerRoute

private enum class TopDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    TIMER("timer", "Focus", Icons.Filled.Timer),
    HABITS("habits", "Habits", Icons.Filled.LocalFireDepartment),
    STATS("stats", "Stats", Icons.Filled.BarChart),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
}

private const val ROUTE_PAYWALL = "paywall"

@Composable
fun EmberFocusApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute != ROUTE_PAYWALL

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = backStackEntry?.destination
                    TopDestination.entries.forEach { dest ->
                        val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopDestination.TIMER.route,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable(TopDestination.TIMER.route) {
                TimerRoute(onRequestPro = { navController.navigate(ROUTE_PAYWALL) })
            }
            composable(TopDestination.HABITS.route) {
                HabitsScreen(
                    onOpenPaywall = { navController.navigate(ROUTE_PAYWALL) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            composable(TopDestination.STATS.route) {
                StatsScreen(
                    onOpenPaywall = { navController.navigate(ROUTE_PAYWALL) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            composable(TopDestination.SETTINGS.route) {
                SettingsScreen(onOpenPaywall = { navController.navigate(ROUTE_PAYWALL) })
            }
            composable(ROUTE_PAYWALL) {
                PaywallScreen(onClose = { navController.popBackStack() })
            }
        }
    }
}
