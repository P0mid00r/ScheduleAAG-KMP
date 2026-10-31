package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bhuvaneshw.pdf.FindController
import com.bhuvaneshw.pdf.PdfListener
import com.bhuvaneshw.pdf.compose.rememberFilePdfState
import com.bhuvaneshw.pdf.compose.ui.PdfScrollBar
import com.bhuvaneshw.pdf.compose.ui.PdfViewer
import com.bhuvaneshw.pdf.compose.ui.PdfViewerContainer
import com.pomidorka.scheduleaag.schedule.PdfDownloader
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.utils.AppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.io.File

@Throws(IOException::class)
suspend fun PdfDownloader.downloadFile(): File {
    val bytes = loadBytes() ?: throw IOException("Ошибка при загрузке данных")
    val fileName = urlPdf.split('/').last().removeSuffix(".pdf")
    return withContext(Dispatchers.IO) {
        File.createTempFile(fileName, ".pdf", AppContext.applicationContext.cacheDir).apply {
            writeBytes(bytes)
        }
    }
}

@Composable
actual fun PdfViewer(
    modifier: Modifier,
    searchTextState: String,
    downloader: PdfDownloader,
) {
    var file: File? by rememberSaveable { mutableStateOf(null) }
    val pdfState = file?.let {
        rememberFilePdfState(it)
    }

    var findController: FindController? by remember { mutableStateOf(null) }

    LaunchedEffect(downloader.urlPdf) {
        try {
            file = downloader.downloadFile()
        } catch (e: Exception) {
            e.message?.let { error ->
                downloader.sendErrorEvent(error)
            }
        }
    }

    LaunchedEffect(searchTextState, findController != null) {
        if (findController != null) {
            if (searchTextState.isEmpty()) {
                findController?.stopFind()
            } else {
                findController?.startFind(searchTextState)
            }
        }
    }

    pdfState?.let { state ->
        PdfViewerContainer(
            pdfState = state,
            pdfViewer = {
                PdfViewer(
                    modifier = modifier,
                    onCreateViewer = {
                        addListener(object : PdfListener {
//                        override fun onDoubleClick() {
//                            callSafely {
//                                if (isZoomInMinScale()) zoomToMaximum()
//                                else zoomToMinimum()
//                            }
//                        }
                            override fun onPageLoadStart() {
                            }

                            override fun onPageLoadSuccess(pagesCount: Int) {
                                findController = pdfState.pdfViewer?.findController
                                file!!.delete()
                            }

                            override fun onPageLoadFailed(errorMessage: String) {
                                downloader.sendErrorEvent(errorMessage)
                            }
                        })
                    },
                )
            },
            pdfScrollBar = { parentSize ->
                PdfScrollBar(
                    parentSize = parentSize,
                    contentColor = Color.White,
                    handleColor = Brown
                )
            }
        )
    }
}