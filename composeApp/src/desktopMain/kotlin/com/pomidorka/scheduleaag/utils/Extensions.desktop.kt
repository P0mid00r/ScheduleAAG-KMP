package com.pomidorka.scheduleaag.utils

import java.awt.Desktop
import java.net.URI

actual fun String.openUrl() {
    Desktop.getDesktop().browse(URI(this))
}