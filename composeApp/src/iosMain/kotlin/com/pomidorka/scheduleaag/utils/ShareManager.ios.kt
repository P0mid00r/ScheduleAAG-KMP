package com.pomidorka.scheduleaag.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.*
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

class IOSShareManager : ShareManager {
    override suspend fun shareImage(image: ImageBitmap): Result<Unit> {
        return runCatching {
            val uiImage = image.toUIImage()

            val activityViewController = UIActivityViewController(
                activityItems = listOf(uiImage),
                applicationActivities = null
            )

            val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootController?.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun ImageBitmap.toUIImage(): UIImage? {
        val width = this.width
        val height = this.height
        val buffer = IntArray(width * height)

        this.readPixels(buffer)

        // Создаем буфер в правильном формате для iOS (RGBA)
        val rgbaBuffer = ByteArray(width * height * 4)

        for (i in 0 until width * height) {
            val argb = buffer[i]
            // ARGB -> RGBA преобразование
            val a = (argb shr 24) and 0xFF
            val r = (argb shr 16) and 0xFF
            val g = (argb shr 8) and 0xFF
            val b = argb and 0xFF

            val index = i * 4
            rgbaBuffer[index] = r.toByte()
            rgbaBuffer[index + 1] = g.toByte()
            rgbaBuffer[index + 2] = b.toByte()
            rgbaBuffer[index + 3] = a.toByte()
        }

        val colorSpace = CGColorSpaceCreateDeviceRGB()

        return memScoped {
            rgbaBuffer.usePinned { pinned ->
                val context = CGBitmapContextCreate(
                    data = pinned.addressOf(0),
                    width = width.convert(),
                    height = height.convert(),
                    bitsPerComponent = 8.convert(),
                    bytesPerRow = (width * 4).convert(),
                    space = colorSpace,
                    bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
                )

                val cgImage = CGBitmapContextCreateImage(context)
                cgImage?.let { UIImage.imageWithCGImage(it) }
            }
        }
    }
}

@Composable
internal actual fun rememberShareManager(): ShareManager = remember { IOSShareManager() }