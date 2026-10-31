package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.conamobile.pdfkmp.viewer.PdfSource
import com.pomidorka.scheduleaag.schedule.PdfDownloader

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    searchTextState: String,
    downloader: PdfDownloader,
) {
    val fileName = downloader.urlPdf.split("/").last()
    var source by remember { mutableStateOf<PdfSource?>(null) }

    LaunchedEffect(downloader.urlPdf) {
        try {
            val bytes = downloader.loadBytes()
            source = PdfSource.of(bytes!!)
        } catch (e: Exception) {
            e.message?.let { error ->
                downloader.sendErrorEvent(error)
            }
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