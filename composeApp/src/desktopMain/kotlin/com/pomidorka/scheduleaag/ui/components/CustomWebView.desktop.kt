package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kdroidfilter.webview.jsbridge.WebViewJsBridge
import io.github.kdroidfilter.webview.request.RequestInterceptor
import io.github.kdroidfilter.webview.request.WebRequest
import io.github.kdroidfilter.webview.request.WebRequestInterceptResult
import io.github.kdroidfilter.webview.web.*

@Composable
actual fun CustomWebView(
    modifier: Modifier,
    state: WebViewState,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    fun onInterceptUrlRequest(request: WebRequest) {
        navigator.requestInterceptor?.onInterceptUrlRequest(request, navigator)
    }
    val data = (state.content as WebContent.Data).data
    val webState = rememberWebViewStateWithHTMLData(
        data = data,
        mimeType = "text/html",
    )
    val webViewNavigator = rememberWebViewNavigator(
        requestInterceptor = object : RequestInterceptor {
            override fun onInterceptUrlRequest(
                request: WebRequest,
                navigator: WebViewNavigator
            ): WebRequestInterceptResult {
                if (request.url.contains("about:blank")) {
                    return WebRequestInterceptResult.Allow
                } else {
                    val webRequest = WebRequest(request.url)

                    onInterceptUrlRequest(webRequest)
                    return WebRequestInterceptResult.Reject
                }
            }
        }
    )

    WebView(
        modifier = modifier,
        navigator = webViewNavigator,
        state = webState,
        onCreated = onCreated,
        onDispose = onDispose
    )
}