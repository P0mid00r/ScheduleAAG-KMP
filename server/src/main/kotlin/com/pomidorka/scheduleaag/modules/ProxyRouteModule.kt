package com.pomidorka.scheduleaag.modules

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeFully

fun Application.configureProxyRoute() {
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
                    val newResponse = client.get(targetUrl) {
                        call.request.copyRequestToResponse()

                        contentType(call.request.contentType())
                        val body = call.receiveText()
                        setBody(body)
                    }

                    val statusCode = newResponse.status
                    if (isPdfFile) call.respondBytesWriter(
                        contentType = newResponse.contentType(),
                        status = newResponse.status,
                        contentLength = newResponse.contentLength()
                    ) {
                        val channel = newResponse.bodyAsChannel()
                        val buffer = ByteArray(8192)
                        while (!channel.isClosedForRead) {
                            val read = channel.readAvailable(buffer, 0, buffer.size)
                            if (read <= 0) break
                            writeFully(buffer, 0, read)
                        }
                    }
                    else call.respond(statusCode,newResponse.bodyAsText())
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