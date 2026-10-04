package com.pomidorka.scheduleaag.utils

import com.pomidorka.scheduleaag.Strings
import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient

suspend fun <T> HttpClient.executeWithProxy(
    url: String,
    isNeedProxy: Boolean = true,
    block: suspend HttpClient.(String) -> T
): T {
// TODO: Чтобы использовать надо получить сертификат на сервере    if (currentPlatform().type.isWeb) return this.executeWithProxyForWebApp(url, block)
    if (currentPlatform().type.isWeb) return this.block(Strings.SECOND_PROXY_FOR_WEB_APP + url)
    if (!isNeedProxy) return this.block(url)

    return try {
        this.block(Strings.PROXY + url)
    } catch (_: Exception) {
        this.block(url)
    }
}

// TODO: Костыль, но что поделать((
private suspend fun <T> HttpClient.executeWithProxyForWebApp(
    url: String,
    block: suspend HttpClient.(String) -> T
): T {
    return try {
        this.block(Strings.PROXY + url)
    } catch (_: Exception) {
        this.block(Strings.SECOND_PROXY_FOR_WEB_APP + url)
    }
}