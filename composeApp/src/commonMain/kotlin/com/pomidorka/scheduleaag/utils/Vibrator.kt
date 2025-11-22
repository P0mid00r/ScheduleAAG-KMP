package com.pomidorka.scheduleaag.utils

import com.pomidorka.scheduleaag.data.SettingsData

interface Vibrator {
    companion object {
        val isEnabled
            get() = SettingsData.isVibrationActive
    }
    val isVibrateSupported: Boolean
    fun vibrateClick()
    fun vibrate(time: Long)
}

expect fun getVibrator(): Vibrator