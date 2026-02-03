package com.pomidorka.scheduleaag.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pomidorka.scheduleaag.ui.Green

@Composable
fun ShareFloatingActionButton(
    onClick: () -> Unit,
    containerColor: Color = Green
) {
    FloatingActionButton(
        containerColor = containerColor,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            tint = Color.White
        )
    }
}