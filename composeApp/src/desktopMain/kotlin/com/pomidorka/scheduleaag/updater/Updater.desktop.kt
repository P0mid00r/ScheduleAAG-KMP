package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.utils.createHttpClient
import org.jetbrains.skiko.hostOs

internal class DesktopUpdater() : Updater {
    private val gitHubApi = GitHubApi(createHttpClient())

    override suspend fun checkAvailableUpdates(): Updates? {
        try {
            val releases = getReleases() ?: return null
            val gitHubVersion = releases.getVersionTag().parseVersion()
            val currentVersion = Updater.CURRENT_VERSION_NAME.parseVersion()

            return if (gitHubVersion > currentVersion) {
                Updates(
                    versionName = gitHubVersion.toString(),
                    whatsNew = releases.getWhatsNew(),
                    url = releases.getDownloadUrl(),
                )
            } else null
        } catch (_: Exception) {
            return null
        }
    }

    private suspend fun getReleases() = gitHubApi.getReleases()

    private fun ReleasesData.getVersionTag() = this.tag_name

    private fun ReleasesData.getWhatsNew() = this.body

    private fun ReleasesData.getDownloadUrl(): String {
        return this.assets.find {
            when {
                hostOs.isWindows -> {
//                    if (hostArch == Arch.X64) { }
                    it.name.endsWith(".msi")
                }

                hostOs.isMacOS -> {
                    it.name.endsWith(".dmg")
                }

                hostOs.isLinux -> {
                    it.name.endsWith(".deb")
                }

                else -> throw NotImplementedError()
            }
        }?.browser_download_url ?: gitHubApi.urlReleases
    }
}

actual fun getUpdater(): Updater = DesktopUpdater()