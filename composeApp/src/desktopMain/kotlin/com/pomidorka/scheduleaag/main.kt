package com.pomidorka.scheduleaag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.Log
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.BasicTitleBar
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.styling.LocalTitleBarStyle
import dev.nucleusframework.window.styling.TitleBarColors
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostArch
import org.jetbrains.skiko.hostOs
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.new_logo_vika
import kotlin.time.Duration.Companion.seconds

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


    nucleusApplication(backend = NucleusBackend.Tao) {
        DecoratedWindow(
            title = "Расписание".plus(if (isRelease) "" else " DEBUG $hostArch"),
            icon = painterResource(Res.drawable.new_logo_vika),
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(450.dp, 700.dp)
            ),
            resizable = true,
            onCloseRequest = ::exitApplication,
            minimumSize = DpSize(450.dp, 700.dp),
        ) {
            var splashLoaded by remember { mutableStateOf(false) }
            val titleBarHeight = (nucleusWindow.boundsOnScreen()!!.height / 16).dp
            LaunchedEffect(Unit) {
                timer(1) {
                    splashLoaded = true
                }
            }

            WindowScaffold(
                titleBar = {
                    BasicTitleBar(
                        modifier = Modifier
                            .height(titleBarHeight),
                        style = LocalTitleBarStyle.current.copy(
                            colors = TitleBarColors(
                                background = Green,
                                content = Color.White,
                                inactiveBackground = Green,
                                border = Color.Unspecified,
                            )
                        )
                    )
                }
            ) { paddingValues ->
                Box(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
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
    }
}

suspend fun timer(
    seconds: Int,
    onComplete: () -> Unit
) {
    for (i in 1..seconds) {
        delay(1.seconds)
    }
    onComplete()
}
