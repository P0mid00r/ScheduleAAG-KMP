package com.pomidorka.scheduleaag.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.fleeksoft.io.ByteArrayOutputStream
import com.pomidorka.scheduleaag.utils.Log
import com.pomidorka.scheduleaag.utils.createHttpClient
import com.pomidorka.scheduleaag.utils.executeWithProxy
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.io.IOException

@Composable
fun rememberPdfDownloader(
    urlPdf: String,
    pdfDownloaderListener: PdfDownloaderListener? = null
) = remember(urlPdf) {
    PdfDownloader(
        urlPdf = urlPdf,
        pdfDownloaderListener = pdfDownloaderListener
    )
}

@Stable
class PdfDownloader(
    val urlPdf: String,
    private val pdfDownloaderListener: PdfDownloaderListener? = null,
) {
    val pdfLoadingState: StateFlow<PdfLoadingState>
        field = MutableStateFlow<PdfLoadingState>(PdfLoadingState.Initialization)

    fun sendErrorEvent(text: String) {
        pdfLoadingState.update {
            PdfLoadingState.Failed(Throwable(text))
        }
    }

    suspend fun loadBytes(): ByteArray? {
        pdfDownloaderListener?.onInitialization()
        pdfLoadingState.update {
            PdfLoadingState.Initialization
        }

        val client = createHttpClient()

        return try {
            val bytes = pdfCache.getOrPutConcurrent(urlPdf) {
                client.executeWithProxy(urlPdf) { url ->
                    val statement = prepareGet(url) {
                        onDownload { bytesSendTotal, contentLength ->
                            val percent = (bytesSendTotal.toDouble() / contentLength!! * 100).toFloat()
                            pdfDownloaderListener?.onLoading(percent)
                            pdfLoadingState.update {
                                PdfLoadingState.Loading(percent)
                            }
                        }
                    }

                    statement.execute { response ->
                        if (!response.status.isSuccess()) {
                            throw IOException("Ошибка загрузки: ${response.status}")
                        }

                        pdfDownloaderListener?.onStarted()
                        pdfLoadingState.update {
                            PdfLoadingState.Started
                        }

                        pdfDownloaderListener?.onLoading(0f)
                        pdfLoadingState.update {
                            PdfLoadingState.Loading(0f)
                        }

                        val channel: ByteReadChannel = response.bodyAsChannel()
                        val initialCapacity = response.contentLength()
                            ?.coerceIn(0, Int.MAX_VALUE.toLong())
                            ?.toInt()
                            ?: (256 * 1024) // 256 КБ

                        val output = ByteArrayOutputStream(initialCapacity)
                        val chunk = ByteArray(64 * 1024) // 64 КБ

                        while (!channel.isClosedForRead) {
                            val bytesRead = channel.readAvailable(chunk, 0, chunk.size)
                            if (bytesRead <= 0) break

                            output.write(chunk, 0, bytesRead)
                        }

                        return@execute output.toByteArray()
                    }
                }
            }

            pdfDownloaderListener?.onLoading(100f)
            pdfLoadingState.update {
                PdfLoadingState.Loading(100f)
            }

            pdfDownloaderListener?.onLoaded()
            pdfLoadingState.update {
                PdfLoadingState.Loaded
            }

            bytes
        } catch (e: Exception) {
            Log.error("PdfViewer.kt") { e.stackTraceToString() }
            pdfDownloaderListener?.onFailed(e)
            pdfLoadingState.update {
                PdfLoadingState.Failed(e)
            }
            null
        } finally {
            client.close()
        }
    }

    companion object {
        private val pdfCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        private val pdfCache: PdfMemoryCache = PdfMemoryCache(maxSize = 15, pdfCoroutineScope)
    }
}

interface PdfDownloaderListener {
    fun onInitialization()
    fun onStarted()
    fun onLoading(progress: Float)
    fun onLoaded()
    fun onFailed(throwable: Throwable)
}

sealed class PdfLoadingState {
    data object Initialization : PdfLoadingState()
    data object Started : PdfLoadingState()
    data class Loading(val progress: Float) : PdfLoadingState()
    data object Loaded : PdfLoadingState()
    data class Failed(val error: Throwable) : PdfLoadingState()
}