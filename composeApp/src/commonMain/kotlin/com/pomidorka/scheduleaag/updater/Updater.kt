package com.pomidorka.scheduleaag.updater

import androidx.compose.runtime.Stable

interface Updater {
    suspend fun checkAvailableUpdates(): Updates?

    companion object {
        const val CURRENT_VERSION_NAME = AppConfig.VERSION_NAME
        const val CURRENT_VERSION_CODE = AppConfig.VERSION_CODE
    }
}

@Stable
data class Updates(
    val versionName: String,
    val whatsNew: String,
    val url: String,
)

expect fun getUpdater(): Updater