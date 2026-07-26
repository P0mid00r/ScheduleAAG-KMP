package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.pomidorka.scheduleaag.utils.currentPlatform

@Composable
fun SchedulePdfViewerScreen(
    navController: NavHostController,
    url: String
) {
    val isNotDesktopOrWeb = !(currentPlatform().type.isDesktop || currentPlatform().type.isWeb)
    var isShowPdfViewer by rememberSaveable { mutableStateOf(true) }
    val loadingDialogController = LoadingDialogController(Strings.PROGRESS_DIALOG_SCHEDULE)
    val errorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            isShowPdfViewer = false
            navController.popBackStack()
        }
    )

    var showFindDialog by rememberSaveable { mutableStateOf(false) }
    var searchTextState by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

    if (showFindDialog) {
        AlertDialog(
            onDismissRequest = { showFindDialog = false },
            title = { Text("Поиск") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Введите запрос") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    searchTextState = searchQuery.trim()
                    showFindDialog = false
                }) {
                    Text("Искать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFindDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Расписание",
                onBackClick = {
                    isShowPdfViewer = false
                    navController.popBackStack()
                },
                actions = {
                    if (isNotDesktopOrWeb) {
                        IconButton(
                            onClick = {
                                showFindDialog = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
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
        BackgroundCells(
            Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            if (isShowPdfViewer) {
                PdfViewer(
                    modifier = Modifier.fillMaxSize(),
                    urlPdf = url,
                    searchTextState = searchTextState,
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