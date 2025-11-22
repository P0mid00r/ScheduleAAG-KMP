package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.WebElementView
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.PlatformWebViewParams
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.pomidorka.scheduleaag.Strings
import kotlinx.browser.document
import org.w3c.dom.HTMLIFrameElement

@OptIn(ExperimentalComposeUiApi::class)
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
    WebElementView(
        factory = {
            (document.createElement("iframe")
                    as HTMLIFrameElement).apply {
                style.apply {
                    border = "none"
                    width = "100%"
                    height = "100%"
                }
                srcdoc = (state.content as WebContent.Data).data
            }
        },
        modifier = modifier,
        update = { iframe -> iframe.srcdoc = iframe.srcdoc }
    )
}