package com.pomidorka.scheduleaag.utils

internal class DesktopVibrator : Vibrator {
    override val isVibrateSupported: Boolean
        get() = false

    override fun vibrateClick() {
    }

    override fun vibrate(time: Long) {
    }
}

actual fun getVibrator(): Vibrator = DesktopVibrator()