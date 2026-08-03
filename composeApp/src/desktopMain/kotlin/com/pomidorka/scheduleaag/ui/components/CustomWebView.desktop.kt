package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kdroidfilter.webview.jsbridge.WebViewJsBridge
import io.github.kdroidfilter.webview.web.WebView
import io.github.kdroidfilter.webview.web.WebViewNavigator
import io.github.kdroidfilter.webview.web.WebViewState

@Composable
actual fun CustomWebView(
    modifier: Modifier,
    state: WebViewState,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    WebView(
        modifier = modifier,
        navigator = navigator,
        state = state,
        webViewJsBridge = webViewJsBridge,
        onCreated = onCreated,
        onDispose = onDispose
    )
}