package com.pomidorka.scheduleaag.ui.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object MainScreen

    @Serializable
    data object SettingsScreen

    @Serializable
    data object ScheduleInteractiveScreen

    @Serializable
    data object CallsScreen

    @Serializable
    data class ScheduleViewerScreen(
        val filterTypeName: String,
        val scheduleTypeName: String
    )

    @Serializable
    data class SchedulePdfViewerScreen(
        val url: String
    )

    @Serializable
    data object ScheduleDaysSelectorScreen
}