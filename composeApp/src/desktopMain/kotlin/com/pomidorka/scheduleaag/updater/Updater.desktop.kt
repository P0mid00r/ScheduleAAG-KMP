package com.pomidorka.scheduleaag.updater

import com.pomidorka.scheduleaag.updater.github.Asset
import com.pomidorka.scheduleaag.updater.github.GitHubApi
import com.pomidorka.scheduleaag.updater.github.ReleasesData
import com.pomidorka.scheduleaag.utils.Log
import com.pomidorka.scheduleaag.utils.OperatingSystem
import com.pomidorka.scheduleaag.utils.createHttpClient
import com.pomidorka.scheduleaag.utils.getOperatingSystem
import io.github.kdroidfilter.platformtools.appmanager.AppManager.applicationExecutablePath
import io.github.kdroidfilter.platformtools.appmanager.getAppInstaller
import io.github.kdroidfilter.platformtools.appmanager.restartApplication
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.remaining
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.asSink
import org.jetbrains.skiko.hostOs
import java.io.File
import kotlin.math.roundToInt
import kotlin.system.exitProcess

private const val TAG = "Updater.desktop.kt"

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

internal suspend fun HttpClient.downloadFile(
    url: String,
    path: String,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    progress: ((Int) -> Unit)? = null
) {
    progress?.invoke(0)
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

            if (fileSize == null) progress?.invoke(-1)
            else {
                val channel: ByteReadChannel = httpResponse.body()
                var count = 0L
                stream.use {
                    while (!channel.exhausted()) {
                        val chunk = channel.readRemaining(bufferSize)
                        count += chunk.remaining

                        chunk.transferTo(stream)

                        progressDownload = ((100f * count) / fileSize).roundToInt()
                        progress?.invoke(progressDownload)
                    }
                }
            }
        }
        onComplete?.invoke()
        Log.info(TAG) { "Скачен файл $fileName" }
    } catch (ex: Exception) {
        progress?.invoke(-1)
        onError?.invoke(ex)
        Log.error(TAG) { ex.toString() }
    }
}

val cacheDir = File(
    System.getProperty("java.io.tmpdir") + "/ScheduleAAG_cache"
).apply {
    mkdir()
}

actual suspend fun Updates.update(listener: UpdateProgressListener?) {
    withContext(Dispatchers.IO) {
        val os = getOperatingSystem()

        val appInstaller = getAppInstaller()
        val fileName = url.split('/').last()
        val file = File(cacheDir.absolutePath + "/" + fileName)
        var isDownloaded = false

        createHttpClient().use { client ->
            client.downloadFile(
                url = url,
                path = cacheDir.absolutePath,
                onError = { error -> listener?.onError(error) },
                onComplete = { isDownloaded = true },
                progress = { progress ->
                    listener?.onProgress(progress)
                    if (progress == 100) listener?.onCompleted()
                },
            )
        }

        if (!isDownloaded) return@withContext

        fun installResult(success: Boolean, message: String?) {
            if (success) {
                Log.info(TAG) { "App installed successfully." }
                file.delete()
                Log.info(TAG) { "Deleted $file" }
                listener?.onCompleted()
                restartApplication()
            } else {
                listener?.onError(Throwable(message))
                Log.error(TAG) { "Failed to install app: $message" }
            }
        }

        when (os) {
            OperatingSystem.MACOS -> installAppOnMac(file) { success, message ->
                installResult(success, message)
            }

            else -> appInstaller.installApp(file) { success, message ->
                installResult(success, message)
            }
        }
    }
}

// TODO: Доделать чтобы перезапускался именно .app, а не как в restartApplication()
private fun restartApplicationOnMac() {
    try {
        val appPath = applicationExecutablePath.substringBeforeLast(".app") + ".app"
        val processBuilder = ProcessBuilder("open $appPath")
        processBuilder.start()
        exitProcess(0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private suspend fun installAppOnMac(dmgFile: File, onResult: (Boolean, String?) -> Unit) {
    if (!dmgFile.exists() && dmgFile.extension != "dmg") {
        onResult(false, "File ${dmgFile.absolutePath} does not exist dmg.")
        return
    }

    val appName = "ScheduleAAG.app"
    val tempMount = "/Volumes/ScheduleAAG"
    val targetDir = applicationExecutablePath.substringBeforeLast(".app") + ".app"

    val scriptContent = $$"""
        DMG_PATH="$${dmgFile.absolutePath}"
        APP_NAME="$$appName"
        TARGET_DIR="$$targetDir"
        TEMP_MOUNT="$$tempMount"

        echo "Монтирование DMG..."
        expect << EOF
        spawn hdiutil attach "$DMG_PATH" -mountpoint "$TEMP_MOUNT" -nobrowse
        expect {
            -re {Agree Y/N\?} { send "Y\r"; exp_continue }
            -re {(:|\(END\))} { send "\r"; exp_continue }
            eof
        }
        EOF

        # Проверка, что том смонтирован
        if [ ! -d "$TEMP_MOUNT" ]; then
            echo "Ошибка: том не смонтирован"
            exit 1
        fi
    
        # Проверка наличия приложения в образе
        if [ ! -d "$TEMP_MOUNT/$APP_NAME" ]; then
            echo "Ошибка: приложение $APP_NAME не найдено в образе"
            echo "Содержимое тома:"
            ls -la "$TEMP_MOUNT"
            exit 1
        fi

        echo "Закрытие старой версии..."
        pkill -f "$APP_NAME" 2>/dev/null || true

        echo "Удаление старой версии..."
        rm -rf "$TARGET_DIR"

        echo "Копирование новой версии..."
        cp -R "$TEMP_MOUNT/$APP_NAME" "$TARGET_DIR"

        echo "Снятие карантина..."
        xattr -d com.apple.quarantine "$TARGET_DIR" 2>/dev/null || true

        echo "Отмонтирование DMG..."
        hdiutil detach "$TEMP_MOUNT"

        echo "Запуск обновлённого приложения..."
        open "$TARGET_DIR"

    """.trimIndent()

    withContext(Dispatchers.IO) {
        val exitCode = ProcessBuilder(
            "bash",
            "-c",
            scriptContent,
        ).start()
            .waitFor()

        if (exitCode == 0) {
            onResult(true, null)
        } else {
            onResult(false, "Ошибка при установке, возможно вы не выдали доступ к папке для процесса обновления!")
        }
    }
}

actual fun getUpdater(): Updater = DesktopUpdater()