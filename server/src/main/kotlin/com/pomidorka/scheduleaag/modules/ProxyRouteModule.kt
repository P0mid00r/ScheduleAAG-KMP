package com.pomidorka.scheduleaag.modules

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProxyRoute() {
    routing {
        get("/proxy") {
            call.queryParameters["url"]?.let { url ->
                val targetUrl = if (url.startsWith("http://", ignoreCase = true) ||
                    url.startsWith("https://", ignoreCase = true)
                ) url else "https://$url"

                runCatching {
                    val newResponse = client.get(targetUrl) {
                        call.request.headers.forEach { name, value ->
                            if (name != "Host" && name != "Accept-Encoding") {
                                headers.appendAll(name, value)
                            }
                        }
                        val body = call.receiveText()
                        setBody(body)
                    }

                    val statusCode = newResponse.status
                    call.respond(statusCode,newResponse.bodyAsText())
                }.onFailure { call.respond(HttpStatusCode.BadRequest) }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        post("/proxy") {
            call.queryParameters["url"]?.let { url ->
                val targetUrl = if (url.startsWith("http://", ignoreCase = true) ||
                    url.startsWith("https://", ignoreCase = true)
                ) url else "https://$url"

                runCatching {
                    val newResponse = client.post(targetUrl) {
                        call.request.headers.forEach { name, value ->
                            if (name != "Host" && name != "Accept-Encoding") {
                                headers.appendAll(name, value)
                            }
                        }
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