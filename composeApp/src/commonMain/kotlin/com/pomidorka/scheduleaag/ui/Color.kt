package com.pomidorka.scheduleaag.ui

import androidx.compose.ui.graphics.Color

val Green = Color(0, 146, 71)
val Brown = Color(191, 159, 98)

fun Color.toHexString(): String {
    val hex = value.toString(16)
        .padStart(8, '0')
        .uppercase()
        .apply {
            removeRange(8, length)
        }
        .substring(2)
    return "#$hex"
}