package com.pomidorka.scheduleaag.modules

import com.pomidorka.scheduleaag.schedule.PdfMemoryCache
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.ByteArrayOutputStream

fun Application.configureProxyRoute() {
    val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO )
    val pdfCache = PdfMemoryCache(maxSize = 300, scope = cacheScope)

    routing {
        get("/proxy") {
            call.queryParameters["url"]?.let { url ->
                val targetUrl = url.toUrlFormat()

                if (!targetUrl.isUrlAllowed()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val isPdfFile = url.endsWith(".pdf")

                runCatching {
                    if (isPdfFile) {
                        val pdfBytes = pdfCache.getOrPutConcurrent(targetUrl) {
                            loadPdfBytes(targetUrl)
                        }
                        call.respondBytes(pdfBytes, call.request.contentType())
                    } else {
                        val newResponse = client.get(targetUrl) {
                            call.request.copyRequestToResponse()

                            contentType(call.request.contentType())
                            val body = call.receiveText()
                            setBody(body)
                        }
                        val statusCode = newResponse.status
                        call.respond(statusCode,newResponse.bodyAsText())
                    }
                }.onFailure { call.respond(HttpStatusCode.BadRequest) }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        post("/proxy") {
            call.queryParameters["url"]?.let { url ->
                val targetUrl = url.toUrlFormat()

                if (!targetUrl.isUrlAllowed()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                runCatching {
                    val newResponse = client.post(targetUrl) {
                        call.request.copyRequestToResponse()
                        val body = call.receiveText()
                        setBody(body)
                    }

                    val statusCode = newResponse.status
                    call.respond(statusCode,newResponse.bodyAsText())
                }.onFailure { call.respond(HttpStatusCode.BadRequest) }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}

private suspend fun loadPdfBytes(url: String): ByteArray {
    val response = client.get(url)
    val channel = response.bodyAsChannel()
    val buffer = ByteArray(8192)
    val output = ByteArrayOutputStream()
    while (!channel.isClosedForRead) {
        val read = channel.readAvailable(buffer, 0, buffer.size)
        if (read <= 0) break
        output.write(buffer, 0, read)
    }
    return output.toByteArray()
}

private fun String.toUrlFormat(): String {
    val targetUrl = if (this.startsWith("http://", ignoreCase = true) ||
        this.startsWith("https://", ignoreCase = true)
    ) this else "https://$this"

    return targetUrl
}

private fun String.isUrlAllowed(): Boolean {
    return this.contains("altag.ru")
}

context(httpRequestBuilder: HttpRequestBuilder)
private fun RoutingRequest.copyRequestToResponse() {
    call.request.headers.forEach { name, value ->
        if (name != "Host" && name != "Accept-Encoding") {
            httpRequestBuilder.headers.appendAll(name, value)
        }
    }

    val cookies = call.request.headers[HttpHeaders.Cookie]
    if (cookies != null) {
        httpRequestBuilder.header(HttpHeaders.Cookie, cookies)
    }

    httpRequestBuilder.header("Access-Control-Allow-Origin", "https://scheduleaag.github.io")
}