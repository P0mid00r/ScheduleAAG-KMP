package com.pomidorka.scheduleaag.utils

import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

object InternetConnection {
    private val client = createHttpClient()

    suspend fun checkInternetConnection(): Boolean {
        return checkUrlConnection("https://altag.ru/")
    }

    suspend fun checkInternetConnectionRuStore(): Boolean {
        return checkUrlConnection("https://apps.rustore.ru/app/com.pomidorka.scheduleaag")
    }

    suspend fun checkUrlConnection(url: String): Boolean {
        return try {
            val responseStatus = client.post(url).status
            responseStatus == HttpStatusCode.OK
        } catch(_: Exception) {
            false
        }
    }
}