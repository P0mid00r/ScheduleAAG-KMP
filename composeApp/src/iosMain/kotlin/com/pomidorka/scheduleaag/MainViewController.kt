package com.pomidorka.scheduleaag

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import com.pomidorka.scheduleaag.utils.Log

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        enableBackGesture = true
    }
) {
    Log.info("MainViewController.kt") { "Запуск iOS приложения" }

    App()
}