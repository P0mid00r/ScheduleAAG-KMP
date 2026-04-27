package com.pomidorka.scheduleaag

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.pomidorka.scheduleaag.ui.screens.ScheduleInteractiveScreenForStand
import com.pomidorka.scheduleaag.utils.Log
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.url.URLSearchParams

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
fun main() {
    Log.info("main.kt") { "Запуск веб приложения" }

    val params = URLSearchParams(window.location.search.toJsString())
    val isStand = params.has("ui-for-stand")

    ComposeViewport(document.body!!) {
        val scope = rememberCoroutineScope()
        scope.launch {
            delay(2000)
            hideSplash()
        }

        if (isStand) {
            ScheduleInteractiveScreenForStand()
        } else {
            App()
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
fun hideSplash() {
    js("splashScreenControl.hideSplashScreen()")
}