package com.pomidorka.scheduleaag.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Stable
interface ShareManager {
    suspend fun shareImage(image: ImageBitmap): Result<Unit>
}

@Composable
internal expect fun rememberShareManager(): ShareManager