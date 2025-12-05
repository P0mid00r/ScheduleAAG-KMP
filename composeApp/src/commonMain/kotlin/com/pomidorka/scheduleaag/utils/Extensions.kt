package com.pomidorka.scheduleaag.utils

import com.multiplatform.webview.web.WebViewState
import com.pomidorka.scheduleaag.Strings

fun WebViewState.isErrorRequest(): Boolean {
    this.errorsForCurrentRequest.let {
        return it.isNotEmpty() && it.last { error ->
            error.isFromMainFrame
        }.code != 200
    }
}

fun String.addProxyInUrl() = Strings.PROXY.plus(this)

expect fun String.openUrl()