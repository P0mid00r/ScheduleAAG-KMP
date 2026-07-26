package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bhuvaneshw.pdf.FindController
import com.bhuvaneshw.pdf.PdfListener
import com.bhuvaneshw.pdf.PdfUnstableApi
import com.bhuvaneshw.pdf.compose.rememberPdfState
import com.bhuvaneshw.pdf.compose.ui.PdfScrollBar
import com.bhuvaneshw.pdf.compose.ui.PdfViewer
import com.bhuvaneshw.pdf.compose.ui.PdfViewerContainer
import com.pomidorka.scheduleaag.ui.Brown

@OptIn(PdfUnstableApi::class)
@Composable
actual fun PdfViewer(
    modifier: Modifier,
    urlPdf: String,
    searchTextState: String,
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val pdfState = rememberPdfState(urlPdf)
    var findController: FindController? by remember { mutableStateOf(null) }

    LaunchedEffect(searchTextState, findController != null) {
        if (findController != null) {
            if (searchTextState.isEmpty()) {
                findController?.stopFind()
            } else {
                findController?.startFind(searchTextState)
            }
        }
    }

    PdfViewerContainer(
        pdfState = pdfState,
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
                            onLoading()
                        }

                        override fun onPageLoadSuccess(pagesCount: Int) {
                            onLoaded()
                            findController = pdfState.pdfViewer?.findController
                        }

                        override fun onPageLoadFailed(errorMessage: String) {
                            onError(Throwable(errorMessage))
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