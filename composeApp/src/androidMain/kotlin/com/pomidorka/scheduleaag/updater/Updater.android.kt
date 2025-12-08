package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.utils.openUrl

internal class AndroidUpdater : Updater {
    private val url = "https://apps.rustore.ru/app/com.pomidorka.scheduleaag"

    override suspend fun checkAvailableUpdates(): Updates? {
        return try {
            val versionStore = RuStoreApi.getStoreVersion() ?: return null

            if (versionStore.versionCode > Updater.CURRENT_VERSION_CODE) {
                return Updates(
                    versionName = versionStore.versionName,
                    whatsNew = versionStore.whatsNew,
                    url = url
                )
            } else null
        } catch (_: Exception) {
           null
        }
    }
}

actual fun Updates.update(listener: UpdateProgressListener?) {
    listener?.onProgress(-1)
    this.url.openUrl()
    listener?.onCompleted()
}

actual fun getUpdater(): Updater = AndroidUpdater()