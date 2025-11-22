package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onLoading: () -> Unit,
    onLoaded: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val pdfState = rememberPdfState(urlPdf)

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