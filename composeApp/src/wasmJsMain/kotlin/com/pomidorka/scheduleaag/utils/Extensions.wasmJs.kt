package com.pomidorka.scheduleaag.utils

actual fun String.openUrl() {
    openUrlInWeb(this)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun openUrlInWeb(url: String) {
    js("window.open(url, '_blank');")
}