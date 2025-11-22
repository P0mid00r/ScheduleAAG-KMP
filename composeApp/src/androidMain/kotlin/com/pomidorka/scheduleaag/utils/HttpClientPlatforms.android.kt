package com.pomidorka.scheduleaag.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHttpClient(): HttpClient = HttpClient(Android) {
    engine {
        connectTimeout = 4000
        socketTimeout = 4000
    }
}