package com.pomidorka.scheduleaag.utils

import com.pomidorka.scheduleaag.Strings
import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient

suspend fun <T> HttpClient.executeWithProxy(
    url: String,
    isNeedProxy: Boolean = true,
    block: suspend HttpClient.(String) -> T
): T {
    if (!isNeedProxy) this.block(url)

    return try {
        this.block(Strings.PROXY + url)
    } catch (_: Exception) {
        this.block(url)
    }
}