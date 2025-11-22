package com.pomidorka.scheduleaag.utils

import android.os.VibrationEffect

internal class AndroidVibrator : Vibrator {
    internal companion object {
        lateinit var vibrator: android.os.Vibrator
    }

    override val isVibrateSupported: Boolean
        get() = vibrator.hasVibrator()

    override fun vibrateClick() {
        vibrate(15)
    }

    override fun vibrate(time: Long) {
        if (isVibrateSupported && Vibrator.isEnabled) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.EFFECT_TICK))
        }
    }
}

actual fun getVibrator(): Vibrator = AndroidVibrator()