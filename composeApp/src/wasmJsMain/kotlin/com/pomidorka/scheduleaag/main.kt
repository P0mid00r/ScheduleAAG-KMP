package com.pomidorka.scheduleaag

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.pomidorka.scheduleaag.utils.Log
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Log.info("main.kt") { "Запуск веб приложения" }

    ComposeViewport(document.body!!) {
        val scope = rememberCoroutineScope()
        scope.launch {
            delay(2000)
            hideSplash()
        }

        App()
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
fun hideSplash() {
    js("splashScreenControl.hideSplashScreen()")
}