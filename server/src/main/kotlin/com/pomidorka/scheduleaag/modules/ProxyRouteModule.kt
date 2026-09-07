package com.pomidorka.scheduleaag.modules

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProxyRoute() {
    routing {
        get("/proxy") {
            call.queryParameters["url"]?.let { url ->
                val targetUrl = if (url.startsWith("http://", ignoreCase = true) ||
                    url.startsWith("https://", ignoreCase = true)
                ) url else "https://$url"

                val newResponse = client.get(targetUrl)
                val statusCode = newResponse.status
                call.respond(statusCode,newResponse.bodyAsText())
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}