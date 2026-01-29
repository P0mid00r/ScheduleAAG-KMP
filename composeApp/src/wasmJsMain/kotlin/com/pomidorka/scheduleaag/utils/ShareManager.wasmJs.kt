package com.pomidorka.scheduleaag.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64

class WasmJSShareManager : ShareManager {
    @OptIn(ExperimentalWasmJsInterop::class)
    override suspend fun shareImage(image: ImageBitmap): Result<Unit> {
        return runCatching {
            val base64 = image.toBase64()
            val isSuccess = copyToClipboard(base64).toBoolean()

            if (!isSuccess) {
                throw UnsupportedOperationException("Не удается скопировать в буфер обмена!")
            }
        }
    }

    private fun ImageBitmap.toBase64(): String {
        val bitmap = this.asSkiaBitmap()
        val bytes = Image.makeFromBitmap(bitmap).encodeToData()!!.bytes
        return Base64.encode(bytes)
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun copyToClipboard(base64: String): JsBoolean = js("""
    {
        try {
            const byteCharacters = atob(base64);
            const byteNumbers = new Array(byteCharacters.length);
            
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            
            const byteArray = new Uint8Array(byteNumbers);
            const imageBlob = new Blob([byteArray], { type: 'image/png' });
            const item = new ClipboardItem({
                'image/png': imageBlob
            });
            window.navigator.clipboard.write([item]);
            return true;
        } catch (err) {
            console.error('Failed to copy image:', err);
            return false;
        }
    }
    """)

@Composable
internal actual fun rememberShareManager(): ShareManager = remember { WasmJSShareManager() }