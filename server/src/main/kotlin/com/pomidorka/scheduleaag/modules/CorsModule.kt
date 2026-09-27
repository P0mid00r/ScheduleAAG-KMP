package com.pomidorka.scheduleaag.modules

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowNonSimpleContentTypes = true

        allowHost("scheduleaag.github.io")
        allowHost("127.0.0.1:8080")
        allowHost("localhost:8080")
    }
}
