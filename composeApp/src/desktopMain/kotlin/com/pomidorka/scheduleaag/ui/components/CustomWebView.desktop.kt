package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nucleusframework.webview.jsbridge.WebViewJsBridge
import dev.nucleusframework.webview.web.NativeWebView
import dev.nucleusframework.webview.web.WebView
import dev.nucleusframework.webview.web.WebViewNavigator
import dev.nucleusframework.webview.web.WebViewState

@Composable
actual fun CustomWebView(
    modifier: Modifier,
    state: WebViewState,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: (NativeWebView) -> Unit,
    onDispose: (NativeWebView) -> Unit,
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