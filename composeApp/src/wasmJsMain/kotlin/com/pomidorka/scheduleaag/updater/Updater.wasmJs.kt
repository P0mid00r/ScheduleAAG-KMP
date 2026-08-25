package com.pomidorka.scheduleaag.updater

internal class WebUpdater() : Updater {
    override suspend fun checkAvailableUpdates(): Updates? = null
}

actual suspend fun Updates.update(listener: UpdateProgressListener?) {
    listener?.onProgress(-1)
    listener?.onCompleted()
}

actual fun getUpdater(): Updater = WebUpdater()