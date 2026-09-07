package com.pomidorka.scheduleaag.modules

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowHost("scheduleaag.github.io")
    }
}
