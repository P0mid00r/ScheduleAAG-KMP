package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialogController
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialogController
import com.pomidorka.scheduleaag.ui.components.schedule.PdfViewer

@Composable
fun SchedulePdfViewerScreen(
    navController: NavHostController,
    url: String
) {
    var isShowPdfViewer by remember { mutableStateOf(true) }
    val loadingDialogController = LoadingDialogController(Strings.PROGRESS_DIALOG_SCHEDULE)
    val errorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            isShowPdfViewer = false
            navController.popBackStack()
        }
    )

    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Расписание",
                onBackClick = {
                    isShowPdfViewer = false
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
        BackgroundCells(
            Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            if (isShowPdfViewer) {
                PdfViewer(
                    modifier = Modifier.fillMaxSize(),
                    urlPdf = url,
                    onLoading = {
                        loadingDialogController.showDialog()
                    },
                    onLoaded = {
                        loadingDialogController.hideDialog()
                    },
                    onError = {
                        errorDialogController.showDialog(it.message!!)
                        loadingDialogController.hideDialog()
                    }
                )
            }
        }
    }
}