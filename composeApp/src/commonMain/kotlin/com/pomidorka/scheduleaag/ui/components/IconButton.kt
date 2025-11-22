package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pomidorka.scheduleaag.utils.getVibrator

@Composable
fun IconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    background: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        background = background,
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = imageVector,
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    painter: Painter,
    background: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        background = background,
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
private fun IconButton(
    onClick: () -> Unit,
    background: Color,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    val vibrator = getVibrator()

    Box(
        modifier = modifier
            .dropShadow(
                shape = CircleShape,
                block = {
                    radius = 10f
                }
            )
            .background(background, CircleShape)
            .clip(CircleShape)
            .clickable {
                onClick.invoke()
                vibrator.vibrateClick()
            },
    ) {
        content()
    }
}