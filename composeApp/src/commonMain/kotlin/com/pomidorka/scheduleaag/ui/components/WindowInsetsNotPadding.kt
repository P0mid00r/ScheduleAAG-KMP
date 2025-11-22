package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WindowInsetsNotPadding : WindowInsets {
    override fun getTop(density: Density): Int = 0

    override fun getBottom(density: Density): Int = 0

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int = 0

    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int = 0
}