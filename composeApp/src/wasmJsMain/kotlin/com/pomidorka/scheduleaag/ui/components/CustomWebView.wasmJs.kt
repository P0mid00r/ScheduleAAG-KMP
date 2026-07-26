package com.pomidorka.scheduleaag.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import io.github.kdroidfilter.webview.jsbridge.WebViewJsBridge
import io.github.kdroidfilter.webview.request.WebRequest
import io.github.kdroidfilter.webview.web.WebContent
import io.github.kdroidfilter.webview.web.WebViewNavigator
import io.github.kdroidfilter.webview.web.WebViewState
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.Event

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
@Composable
actual fun CustomWebView(
    modifier: Modifier,
    state: WebViewState,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    val script = """
        <script>
        document.addEventListener('click', function(e) {
            var link = e.target.closest('a');
            if (link && link.href) {
                e.preventDefault();
                e.stopPropagation();
                // Отправляем сообщение родительскому окну
                window.parent.postMessage(JSON.stringify({
                    type: 'url_intercept',
                    url: link.href
                }), '*');
            }
        }, true);
        </script>
    """.trimIndent()

    DisposableEffect(Unit) {
        val messageHandler: (Event) -> Unit = { event ->
            val messageEvent = event as MessageEvent
            try {
                val rawData = messageEvent.data
                val jsonString = rawData.toString()
                val json = Json.parseToJsonElement(jsonString)
                if (json.jsonObject["type"]?.jsonPrimitive?.content == "url_intercept") {
                    val url = json.jsonObject["url"]?.jsonPrimitive!!.content
                    val webRequest = WebRequest(url)
                    navigator.requestInterceptor?.onInterceptUrlRequest(webRequest, navigator)
                }
            } catch (_: Exception) { }
        }
        window.addEventListener("message", messageHandler)
        onDispose {
            window.removeEventListener("message", messageHandler)
        }
    }

    HtmlElementView(
        factory = {
            (document.createElement("iframe")
                    as HTMLIFrameElement).apply {
                style.apply {
                    border = "none"
                    width = "100%"
                    height = "100%"
                }
                srcdoc = script + (state.content as WebContent.Data).data
            }
        },
        modifier = modifier,
        update = { iframe -> iframe.srcdoc = iframe.srcdoc }
    )
}