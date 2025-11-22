package com.pomidorka.scheduleaag

import AppConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.Log
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostArch
import org.jetbrains.skiko.hostOs
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.new_logo_vika
import java.io.File
import kotlin.math.max

private fun getAppDataDirectory(
    appName: String,
    folder: String
): String {
    return if (hostOs.isMacOS) {
        val userHome = System.getProperty("user.home")
        "$userHome/Library/Application Support/$appName/$folder"
    } else folder
}

private val isRelease = ::main::class.java.protectionDomain.codeSource.location.path.contains(".jar")

fun main() {
    if (hostOs.isMacOS) {
        System.setProperty("apple.awt.application.appearance", "system")
        // Необходима настройка для Catalina+:
        System.setProperty("NSRequiresAquaSystemAppearance", "NO")
    }

    Log.info("main.kt") { "Запуск десктопного приложения" }

    application {
        Window(
            title = AppConfig.APP_NAME.plus(if (isRelease) "" else " DEBUG $hostArch"),
            icon = painterResource(Res.drawable.new_logo_vika),
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(450.dp, 700.dp)
            ),
            resizable = true,
            onCloseRequest = ::exitApplication,
        ) {
            val bundlePath = File(getAppDataDirectory(AppConfig.APP_NAME, "kcef-bundle"))
            val cachePath = File(getAppDataDirectory(AppConfig.APP_NAME, "cache"))

            var restartRequired by remember { mutableStateOf(false) }
            var initialized by remember { mutableStateOf(false) }
            var downloading by remember { mutableStateOf(0F) }

            LaunchedEffect(Unit) {
                if (isRelease) {
                    withContext(Dispatchers.IO) {
                        KCEF.init(
                            builder = {
                                installDir(bundlePath)
                                progress {
                                    onDownloading {
                                        downloading = max(it, 0F)
                                    }
                                    onInitialized {
                                        initialized = true
                                    }
                                }
                                settings {
                                    this.cachePath = cachePath.absolutePath
                                }
                            },
                            onError = {
                                it?.let { throwable ->
                                    Log.error("Main.kt", throwable)
                                    throwable.printStackTrace()
                                }
                            },
                            onRestartRequired = {
                                restartRequired = true
                            }
                        )
                    }
                } else initialized = true
            }

            if (initialized) {
                App()
            } else {
                Box(Modifier.fillMaxSize().background(Green)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier.shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(200.dp)
                            ),
                            painter = painterResource(Res.drawable.new_logo_vika),
                            contentDescription = null,
                        )

                        if (restartRequired) {
                            Text(
                                fontSize = 20.sp,
                                color = Color.White,
                                text = "Перезапустите приложение",
                            )
                        } else {
                            if (downloading > 0) {
                                Text(
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    text = if (downloading == 100f) {
                                        "Запуск приложения..."
                                    } else {
                                        "Загрузка файлов: ${String.format("%.2f", downloading)}%"
                                    },
                                )
                            }
                        }
                    }
                }
            }

            if (isRelease) {
                DisposableEffect(Unit) {
                    onDispose {
                        KCEF.disposeBlocking()
                    }
                }
            }
        }
    }
}
