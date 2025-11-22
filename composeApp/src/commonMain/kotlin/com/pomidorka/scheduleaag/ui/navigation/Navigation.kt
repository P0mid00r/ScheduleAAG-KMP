package com.pomidorka.scheduleaag.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pomidorka.scheduleaag.data.SettingsData
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleType
import com.pomidorka.scheduleaag.ui.screens.*
import com.pomidorka.scheduleaag.utils.Log

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.MainScreen,
    ) {
        composable<Route.MainScreen> {
            MainScreen(navController)
        }

        composable<Route.ScheduleViewerScreen> { stackEntry ->
            val scheduleViewerScreen = stackEntry.toRoute<Route.ScheduleViewerScreen>()
            val scheduleType = ScheduleType.valueOf(scheduleViewerScreen.scheduleTypeName)
            val filterType = FilterType.valueOf(scheduleViewerScreen.filterTypeName)

            ScheduleViewerScreen(
                filterType = filterType,
                scheduleType = scheduleType,
                navController = navController
            )
        }

        composable<Route.SchedulePdfViewerScreen> { stackEntry ->
            val url = stackEntry.toRoute<Route.SchedulePdfViewerScreen>().url

            SchedulePdfViewerScreen(navController, url)
        }

        composable<Route.ScheduleInteractiveScreen> {
            ScheduleInteractiveScreen(navController)
        }

        composable<Route.ScheduleDaysSelectorScreen> {
            val collegeBuilding = SettingsData.selectedCollegeBuilding

            ScheduleDaysSelectorScreen(navController, collegeBuilding)
        }

        composable<Route.CallsScreen> {
            CallsScreen(navController)
        }

        composable<Route.SettingsScreen> {
            SettingsScreen(navController)
        }
    }
}