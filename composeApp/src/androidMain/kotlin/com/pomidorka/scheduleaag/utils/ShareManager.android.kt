package com.pomidorka.scheduleaag.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.fleeksoft.io.ByteArrayOutputStream

class AndroidShareManager(private val context: Context) : ShareManager {
    override suspend fun shareImage(image: ImageBitmap): Result<Unit> {
        return runCatching {
            val byteArray = image.toByteArray()
            val uri = InMemoryImageProvider.registerImage(context, byteArray)

            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
            }

            context.startActivity(
                Intent.createChooser(intent, null)
            )
        }
    }

    private fun ImageBitmap.toByteArray(): ByteArray {
        ByteArrayOutputStream().use {
            val bitmap = this.asAndroidBitmap()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            return it.toByteArray()
        }
    }
}

@Composable
internal actual fun rememberShareManager(): ShareManager = remember { AndroidShareManager(AppContext.activity) }