package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.CustomWebView
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialogController
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialogController
import kotlinx.coroutines.launch

@Composable
fun CallsScreen(navController: NavHostController) {
    var isOffline by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var html: String? by rememberSaveable { mutableStateOf(null) }
    val webViewState = html?.let {
        rememberWebViewStateWithHTMLData(
            data = "<style>${Strings.TABLE_CSS}</style>".plus(it),
            mimeType = "text/html",
        ).apply {
            webSettings.apply {
                backgroundColor = Color.White
                supportZoom = false
            }
        }
    }

    val loadingDialogController = LoadingDialogController(
        message = Strings.PROGRESS_DIALOG_LOADING_PAGE
    )
    val errorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            navController.popBackStack()
        }
    )
    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Расписание звонков",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isOffline) {
                            Text(
                                text = "(офлайн версия)",
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                onBackClick = {
                    html = null
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            Column {
                AdManager.AdBannerAnyScreen(
                    backgroundColor = Green
                )
                NavigationBar(
                    color = Green,
                )
            }
        }
    ) { paddings ->
        LaunchedEffect(Unit) {
            if (html == null) {
                scope.launch {
                    loadingDialogController.showDialog()

                    html = ScheduleApi.getScheduleCallsHtml().let {
                        return@let when(it) {
                            is Result.Success -> it.data
                            is Result.Failure -> null
                        }
                    }

                    loadingDialogController.hideDialog()
                    if (html == null || html == "") {
                        isOffline = true
                        html = Strings.HTML_CALLS
// TODO                       errorDialogController.showDialog(Strings.SITE_CONNECTION_ERROR)
                    }
                }
            }
        }

        Box(
            Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            BackgroundCells(Modifier.fillMaxSize()) {
                webViewState?.let { state ->
                    CustomWebView(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        state = state,
                        captureBackPresses = false
                    )
                }
            }
        }
    }
}