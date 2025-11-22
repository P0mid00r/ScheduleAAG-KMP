package com.pomidorka.scheduleaag.updater

internal class IOSUpdater() : Updater {
    override suspend fun checkAvailableUpdates(): Updates? = null
}

actual fun getUpdater(): Updater = IOSUpdater()