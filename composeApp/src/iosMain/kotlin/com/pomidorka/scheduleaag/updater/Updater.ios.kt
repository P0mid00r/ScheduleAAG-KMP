package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.utils.openUrl

internal class IOSUpdater() : Updater {
    override suspend fun checkAvailableUpdates(): Updates? = null
}

actual fun Updates.update(listener: UpdateProgressListener?) {
    this.url.openUrl()

    listener?.onProgress(-1)
    listener?.onCompleted()
}

actual fun getUpdater(): Updater = IOSUpdater()