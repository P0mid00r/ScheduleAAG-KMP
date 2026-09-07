package com.pomidorka.scheduleaag

import com.pomidorka.scheduleaag.modules.configureCors
import com.pomidorka.scheduleaag.modules.configureProxyRoute
import com.pomidorka.scheduleaag.modules.configureSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::modules)
        .start(wait = true)
}

fun Application.modules() {
    configureCors()
    configureSerialization()
    configureProxyRoute()

    routing {
        get("/") {
            call.respond(message = mapOf("status" to "ok"), status = HttpStatusCode.OK)
        }
    }
}