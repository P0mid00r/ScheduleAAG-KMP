package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.schedule.ScheduleInteractive

@Composable
fun ScheduleInteractiveScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = "Расписание",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            Column {
                AdManager.AdBannerScheduleScreen(
                    backgroundColor = Green
                )
                NavigationBar(
                    color = Green,
                )
            }
        }
    ) { paddings ->
        BackgroundCells(Modifier.fillMaxSize()) {
            ScheduleInteractive(
                modifier = modifier
                    .padding(paddings)
                    .align(Alignment.Center),
                navController = navController,
            )
        }
    }
}