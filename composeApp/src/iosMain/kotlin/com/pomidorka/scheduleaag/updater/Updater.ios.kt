package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.updater.github.Asset
import com.pomidorka.scheduleaag.updater.github.GitHubApi
import com.pomidorka.scheduleaag.updater.github.ReleasesData
import com.pomidorka.scheduleaag.utils.createHttpClient
import com.pomidorka.scheduleaag.utils.openUrl

internal class IOSUpdater() : Updater {
    private val gitHubApi = GitHubApi(createHttpClient())

    override suspend fun checkAvailableUpdates(): Updates? {
        try {
            val releases = loadReleases() ?: return null
            val asset = releases.availableUpdateForCurrentOS() ?: return null

            val gitHubVersion = releases.versionTag.parseVersion()
            val currentVersion = Updater.CURRENT_VERSION_NAME.parseVersion()

            return if (gitHubVersion > currentVersion) {
                Updates(
                    versionName = gitHubVersion.toString(),
                    whatsNew = releases.whatsNew,
                    url = asset.browser_download_url,
                )
            } else null
        } catch (_: Exception) {
            return null
        }
    }

    private fun ReleasesData.availableUpdateForCurrentOS(): Asset? {
        return this.assets.find {
            it.name
                .lowercase()
                .endsWith(".ipa")
        }
    }

    private suspend inline fun loadReleases() = gitHubApi.loadReleases()

    private inline val ReleasesData.versionTag
        get() = this.tag_name

    private inline val ReleasesData.whatsNew
        get() = this.body
}

actual fun Updates.update(listener: UpdateProgressListener?) {
    this.url.openUrl()

    listener?.onProgress(-1)
    listener?.onCompleted()
}

actual fun getUpdater(): Updater = IOSUpdater()