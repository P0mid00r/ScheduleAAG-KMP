package com.pomidorka.scheduleaag.updater

import AppConfig.APP_NAME
import com.pomidorka.scheduleaag.utils.Log
import com.pomidorka.scheduleaag.utils.createHttpClient
import com.pomidorka.scheduleaag.utils.openUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.remaining
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.asSink
import org.jetbrains.skiko.hostOs
import java.io.File
import kotlin.math.roundToInt

internal class DesktopUpdater() : Updater {
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

    private suspend inline fun loadReleases() = gitHubApi.loadReleases()

    private fun ReleasesData.availableUpdateForCurrentOS(): Asset? {
        val suffix = when {
            // TODO: сделать чтобы находило только zip для windows
            hostOs.isWindows -> ".msi"
            hostOs.isMacOS -> ".dmg"
            hostOs.isLinux -> ".deb"
            else -> return null
        }

        return this.assets.find {
            it.name.endsWith(suffix)
        }
    }

    private inline val ReleasesData.versionTag
        get() = this.tag_name

    private inline val ReleasesData.whatsNew
        get() = this.body
}

internal fun HttpClient.downloadFile(
    url: String,
    path: String,
    onError: (Throwable) -> Unit,
    progress: (Int) -> Unit
) = CoroutineScope(Dispatchers.IO).launch {
    progress(0)
    val fileName = url.split('/').last()
    val file = File("$path/$fileName").apply {
        createNewFile()
    }
    val stream = file.outputStream().asSink()
    val bufferSize = 1024L * 1024L

    try {
        prepareGet(url).execute { httpResponse ->
            var progressDownload: Int
            val fileSize: Long? = httpResponse.contentLength()

            if (fileSize == null) progress(-1)
            else {
                val channel: ByteReadChannel = httpResponse.body()
                var count = 0L
                stream.use {
                    while (!channel.exhausted()) {
                        val chunk = channel.readRemaining(bufferSize)
                        count += chunk.remaining

                        chunk.transferTo(stream)

                        progressDownload = ((100f * count) / fileSize).roundToInt()
                        progress(progressDownload)
                    }
                    Log.info("Updater.desktop.kt") { "Скачен файл $fileName" }
                }
            }
        }
    } catch (ex: Exception) {
        progress(-1)
        onError(ex)
        Log.error("Updater.desktop.kt") { ex.toString() }
    }
}

private fun startInstallerForWindows(fileArchive: File) {
    val appFile = File("")
    val batFile = createBatFile(fileArchive, appFile)
    ProcessBuilder("cmd.exe", "/c powershell -Command \"Start-Process '${batFile.canonicalPath}' -Verb RunAs\"")
        .start()
}

private fun getAppPath() {
    val processPath = ProcessHandle.current().info()
}

// TODO Добавить передачу названия файла для обновления
private fun createBatFile(
    fileArchive: File,
    appPath: File
) = File("update.bat").apply {
    val bash = """
@echo off
echo Остановка программы...
taskkill /f /im $APP_NAME.exe
timeout /t 3
echo Распаковка обновления...
tar -xf "${fileArchive.canonicalPath}" -C "${appPath.canonicalPath}" --overwrite
echo Запуск программы...
start "" "${appPath.canonicalPath}/$APP_NAME.exe"
"""
    createNewFile()
    writeText(bash)
}

actual fun Updates.update(listener: UpdateProgressListener?) {
    listener?.onProgress(-1)
    listener?.onCompleted()
    url.openUrl()

//    if (hostOs.isWindows && hostOs.isLinux) {
//        val pathUpdates = "/updates"
//        createHttpClient().downloadFile(
//            url = url,
//            path = pathUpdates,
//            onError = { listener?.onError(it) }
//        ) {
//            listener?.onProgress(it)
//            if (it == 100) listener?.onCompleted()
//        }
//    } else {
//        listener?.onProgress(-1)
//        listener?.onCompleted()
//        url.openUrl()
//    }
}

actual fun getUpdater(): Updater = DesktopUpdater()