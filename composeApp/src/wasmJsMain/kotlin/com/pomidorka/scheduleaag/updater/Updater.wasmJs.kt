package com.pomidorka.scheduleaag.updater

internal class WebUpdater() : Updater {
    override suspend fun checkAvailableUpdates(): Updates? = null
}

actual fun getUpdater(): Updater = WebUpdater()