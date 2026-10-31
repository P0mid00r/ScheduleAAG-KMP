package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import com.pomidorka.scheduleaag.schedule.PdfDownloader
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.*
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFSelection
import platform.PDFKit.PDFView
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PdfViewer(
    modifier: Modifier,
    searchTextState: String,
    downloader: PdfDownloader,
) {
    val pdfView = remember {
        PDFView().apply {
            autoScales = true
        }
    }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(downloader.urlPdf) {
        withContext(Dispatchers.Default) {
            try {
                val nsData = withContext(Dispatchers.IO) {
                    val bytes = downloader.loadBytes()
                    bytes!!.usePinned { pinned ->
                        NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
                    }
                }

                val doc = PDFDocument(data = nsData)

                withContext(Dispatchers.Main) {
                    pdfView.document = doc
                }
                isReady = true
            } catch (e: Exception) {
                e.message?.let { error ->
                    downloader.sendErrorEvent(error)
                }
            }
        }
    }

    LaunchedEffect(searchTextState, isReady) {
        if (!isReady) return@LaunchedEffect

        pdfView.searchAndHighlight(searchTextState)
    }

    UIKitView(
        factory = { pdfView },
        modifier = modifier.background(Color.White),
    )
}

// TODO Да навайбкодил и что?)
private suspend fun PDFView.searchAndHighlight(
    query: String,
    color: UIColor = UIColor.yellowColor,
) {
    if (query.isBlank()) {
        // Очищаем выделения
        highlightedSelections = null
        setNeedsDisplay()
    } else {
        // Выполняем поиск
        val options = NSCaseInsensitiveSearch or NSDiacriticInsensitiveSearch or NSWidthInsensitiveSearch
        val selections = this.document?.findString(query, options) as? NSArray ?: return

        // Преобразуем в массив PDFSelection
        val selectionList = mutableListOf<PDFSelection>()
        for (i in 0 until selections.count.toInt()) {
            val sel = selections.objectAtIndex(i.toULong()) as PDFSelection
            sel.color = color.colorWithAlphaComponent(0.5)
            selectionList.add(sel)
        }

        // Устанавливаем выделения в PDFView (на главном потоке)
        withContext(Dispatchers.Main) {
            highlightedSelections = selectionList
            setNeedsDisplay()
        }
    }
}