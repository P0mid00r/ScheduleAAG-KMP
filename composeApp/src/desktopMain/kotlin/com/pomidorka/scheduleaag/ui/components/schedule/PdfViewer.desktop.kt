package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.conamobile.pdfkmp.viewer.PdfSource
import com.pomidorka.scheduleaag.utils.createHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    urlPdf: String,
    searchTextState: String,
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val fileName = urlPdf.split("/").last()
    var isReady by remember { mutableStateOf(false) }
    var source by remember { mutableStateOf<PdfSource?>(null) }

    LaunchedEffect(urlPdf) {
        onLoading()
        try {
            val client = createHttpClient()
            val bytes = client.get(urlPdf).readRawBytes()
            source = PdfSource.of(bytes)
            client.close()
        } catch (e: Exception) {
            onError(e)
        } finally {
            isReady = true
            onLoaded()
        }
    }

    source?.let {
        com.conamobile.pdfkmp.viewer.PdfViewer(
            source = it,
            shareFileName = fileName,
            modifier = Modifier,
        )
    }
}