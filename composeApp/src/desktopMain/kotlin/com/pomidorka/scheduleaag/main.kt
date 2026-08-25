package com.pomidorka.scheduleaag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostArch
import org.jetbrains.skiko.hostOs
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.new_logo_vika
import java.awt.Dimension

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
            title = "Расписание".plus(if (isRelease) "" else " DEBUG $hostArch"),
            icon = painterResource(Res.drawable.new_logo_vika),
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(450.dp, 700.dp)
            ),
            resizable = true,
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(450, 700)
            val scope = rememberCoroutineScope()
            var splashLoaded by remember { mutableStateOf(false) }
            timer(scope, 1) {
                splashLoaded = true
            }

            if (splashLoaded) {
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
                    }
                }
            }
        }
    }
}

fun timer(scope: CoroutineScope, seconds: Int, onComplete: () -> Unit) {
    scope.launch {
        for (i in 1..seconds) {
            delay(1000L)
        }
        onComplete()
    }
}
