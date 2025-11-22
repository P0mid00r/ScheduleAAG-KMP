package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import com.pomidorka.scheduleaag.Strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSURL
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    urlPdf: String,
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
) {
    onLoading()
    val pdfView = remember {
        PDFView().apply {
            autoScales = true
        }
    }

    UIKitView(
        factory = { pdfView },
        modifier = modifier.background(Color.White),
        update = { view ->
            MainScope().launch {
                withContext(Dispatchers.Default) {
                    try {
                        val nsUrl = NSURL(string = urlPdf)

                        val document = PDFDocument(nsUrl)
                        withContext(Dispatchers.Main) {
                            view.document = document
                        }
                    } catch (_: Exception) {
                        onError(Throwable(Strings.PDF_URL_NOT_FOUND))
                    } finally {
                        onLoaded()
                    }
                }
            }
        }
    )
}