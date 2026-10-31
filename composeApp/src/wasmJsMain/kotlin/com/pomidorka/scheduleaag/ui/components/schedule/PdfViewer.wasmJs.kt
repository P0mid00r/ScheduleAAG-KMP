package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pomidorka.scheduleaag.schedule.PdfDownloader
import com.pomidorka.scheduleaag.ui.components.CustomWebView
import com.pomidorka.scheduleaag.utils.isErrorRequest
import dev.nucleusframework.webview.web.LoadingState
import dev.nucleusframework.webview.web.rememberWebViewStateWithHTMLData

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    searchTextState: String,
    downloader: PdfDownloader,
) {
    val webViewState = rememberWebViewStateWithHTMLData(
        data = getPdfViewerHtml(downloader.urlPdf),
        mimeType = "text/html",
    ).apply {
        webSettings.apply {
            backgroundColor = Color.White
            supportZoom = false
        }
    }

    LaunchedEffect(webViewState.loadingState) {
        when (webViewState.loadingState) {
            LoadingState.Initializing -> {}

            is LoadingState.Loading -> {}

            LoadingState.Finished -> {
                if (webViewState.isErrorRequest()) {
                    downloader.sendErrorEvent("Произошла ошибка при загрузке pdf")
                }
            }
        }
    }

    CustomWebView(
        modifier = modifier.background(Color.Transparent),
        state = webViewState,
    )
}