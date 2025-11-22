package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.pomidorka.scheduleaag.ui.components.CustomWebView
import com.pomidorka.scheduleaag.utils.isErrorRequest

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    urlPdf: String,
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val webViewState = rememberWebViewStateWithHTMLData(
        data = getPdfViewerHtml(urlPdf),
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

            is LoadingState.Loading -> onLoading()

            LoadingState.Finished -> {
                if (webViewState.isErrorRequest()) {
                    onError(Throwable("Произошла ошибка при загрузке pdf"))
                } else onLoaded()
            }
        }
    }

    CustomWebView(
        modifier = modifier.background(Color.Transparent),
        state = webViewState,
        captureBackPresses = false,
    )
}