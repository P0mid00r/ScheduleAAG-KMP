package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.pomidorka.scheduleaag.utils.getImageBase64
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.allDrawableResources
import scheduleaag.composeapp.generated.resources.bgcell
import kotlin.io.encoding.Base64

// TODO: Заменить на imageResource()
private val image = Base64.Default.decode(getImageBase64("bgcell.png")).decodeToImageBitmap()

@Composable
fun BackgroundCells(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)
) {
    val scale = LocalDensity.current.density

    Box(modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val tileWidth = image.width * scale
            val tileHeight = image.height * scale
            val columns = (size.width / tileWidth).toInt() + 1
            val rows = (size.height / tileHeight).toInt() + 1

            for (y in 0 until rows) {
                for (x in 0 until columns) {
                    drawImage(
                        image = image,
                        dstSize = IntSize(tileWidth.toInt(), tileHeight.toInt()),
                        dstOffset = IntOffset((x * tileWidth).toInt(), (y * tileHeight).toInt()),
                        filterQuality = FilterQuality.None
                    )
                }
            }
        }

        content()
    }
}
