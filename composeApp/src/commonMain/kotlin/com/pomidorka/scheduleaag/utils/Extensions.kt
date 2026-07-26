package com.pomidorka.scheduleaag.utils

import com.pomidorka.scheduleaag.Strings
import io.github.kdroidfilter.webview.web.WebViewState

fun WebViewState.isErrorRequest(): Boolean {
    this.errorsForCurrentRequest.let {
        return it.isNotEmpty() && it.last { error ->
            error.isFromMainFrame
        }.code != 200
    }
}

fun String.addProxyInUrl() = Strings.PROXY.plus(this)

expect fun String.openUrl()