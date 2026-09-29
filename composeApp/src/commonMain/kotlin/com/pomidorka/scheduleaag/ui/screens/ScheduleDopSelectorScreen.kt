package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
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
import com.pomidorka.scheduleaag.ui.navigation.Route
import io.github.kdroidfilter.webview.request.RequestInterceptor
import io.github.kdroidfilter.webview.request.WebRequest
import io.github.kdroidfilter.webview.request.WebRequestInterceptResult
import io.github.kdroidfilter.webview.web.WebViewNavigator
import io.github.kdroidfilter.webview.web.rememberWebViewNavigator
import io.github.kdroidfilter.webview.web.rememberWebViewStateWithHTMLData
import kotlinx.coroutines.launch

@Composable
fun ScheduleDopSelectorScreen(
    navController: NavHostController,
) {
    val scope = rememberCoroutineScope()
    var html: String? by rememberSaveable { mutableStateOf(null) }
    val webViewState = html?.let {
        rememberWebViewStateWithHTMLData(
            data = "<style>${Strings.TABLE_CSS}</style>".plus(html)
        ).apply {
            webSettings.apply {
                backgroundColor = Color.White
                supportZoom = false
            }
        }
    }

    val webViewNavigator = rememberWebViewNavigator(
        requestInterceptor =
            object : RequestInterceptor {
                override fun onInterceptUrlRequest(
                    request: WebRequest,
                    navigator: WebViewNavigator
                ): WebRequestInterceptResult {
                    if (request.url.contains("about:blank") || request.url.contains("data")) {
                        return WebRequestInterceptResult.Allow
                    } else {
                        scope.launch {
                            navController.navigate(Route.SchedulePdfViewerScreen(request.url))
                        }

                        return WebRequestInterceptResult.Reject
                    }
                }
            }
    )

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
                title = "Расписание ДОПов",
                onBackClick = {
                    html = null
                    navController.popBackStack()
                },
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

                    html = ScheduleApi.getDopHtml().let {
                        return@let when(it) {
                            is Result.Success -> it.data
                            is Result.Failure -> null
                        }
                    }

                    loadingDialogController.hideDialog()
                    if (html == null || html == "") {
                        errorDialogController.showDialog(Strings.SITE_CONNECTION_ERROR)
                    }
                }
            }
        }

        BackgroundCells(Modifier.fillMaxSize()) {
            webViewState?.let { state ->
                CustomWebView(
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .background(Color.Transparent),
                    state = state,
                    navigator = webViewNavigator,
                )
            }
        }
    }
}