package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PdfViewer(
    modifier: Modifier,
    urlPdf: String,
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
)

fun getPdfViewerHtml(urlPdf: String): String {
    return if (urlPdf.startsWith("http://") || urlPdf.startsWith("https://"))
        "<style>html, body { margin: 0; padding: 0; height: 100vh; width: 100vw; overflow: hidden; }</style>" +
                "<embed src=\"${urlPdf}\" width=\"100%\" height=\"100%\" type=\"application/pdf\"/>"
    else throw IllegalArgumentException()
}