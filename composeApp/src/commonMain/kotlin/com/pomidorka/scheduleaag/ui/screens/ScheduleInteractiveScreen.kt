package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.ShareFloatingActionButton
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialogController
import com.pomidorka.scheduleaag.ui.components.alertdialogs.InfoDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.InfoDialogController
import com.pomidorka.scheduleaag.ui.components.schedule.ScheduleInteractive
import com.pomidorka.scheduleaag.utils.currentPlatform
import com.pomidorka.scheduleaag.utils.rememberScreenshotController
import com.pomidorka.scheduleaag.utils.rememberShareManager
import kotlinx.coroutines.launch

@Composable
fun ScheduleInteractiveScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val shareManager = rememberShareManager()
    val screenshotController = rememberScreenshotController()
    val scope = rememberCoroutineScope()

    val errorShareDialogController = ErrorDialogController {
        it.hideDialog()
    }
    val infoDialogController = InfoDialogController(message = "Расписание скопировано в буфер обмена!")

    InfoDialog(infoDialogController)
    ErrorDialog(errorShareDialogController)

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Расписание",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            ShareFloatingActionButton(
                onClick = {
                    scope.launch {
                        val bitmap = screenshotController.captureAsync().await()
                        shareManager.shareImage(bitmap).let { result ->
                            if (!currentPlatform().type.isMobile) {
                                result
                                    .onSuccess {
                                        infoDialogController.showDialog()
                                    }
                                    .onFailure { error ->
                                        errorShareDialogController.showDialog(error.message ?: "")
                                    }
                            }
                        }
                    }
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
                screenshotController = screenshotController,
                navController = navController,
            )
        }
    }
}