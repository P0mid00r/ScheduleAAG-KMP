package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.web.PlatformWebViewParams
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import io.github.kdroidfilter.webview.request.RequestInterceptor
import io.github.kdroidfilter.webview.request.WebRequestInterceptResult
import io.github.kdroidfilter.webview.web.WebView
import io.github.kdroidfilter.webview.web.rememberWebViewNavigator
import io.github.kdroidfilter.webview.web.rememberWebViewStateWithHTMLData

@Composable
actual fun CustomWebView(
    modifier: Modifier,
    state: WebViewState,
    captureBackPresses: Boolean,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
    platformWebViewParams: PlatformWebViewParams?,
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
                request: io.github.kdroidfilter.webview.request.WebRequest,
                navigator: io.github.kdroidfilter.webview.web.WebViewNavigator
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