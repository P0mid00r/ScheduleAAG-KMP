package com.pomidorka.scheduleaag.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

class DesktopShareManager : ShareManager {
    override suspend fun shareImage(image: ImageBitmap): Result<Unit> {
        return runCatching {
            val bufferedImage = image.toAwtImage()

            val imageSelection = object : Transferable {
                override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
                override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
                override fun getTransferData(flavor: DataFlavor): Any {
                    if (!isDataFlavorSupported(flavor)) {
                        throw UnsupportedFlavorException(flavor)
                    }
                    return bufferedImage
                }
            }

            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(imageSelection, null)
        }
    }
}

@Composable
internal actual fun rememberShareManager(): ShareManager = remember { DesktopShareManager() }