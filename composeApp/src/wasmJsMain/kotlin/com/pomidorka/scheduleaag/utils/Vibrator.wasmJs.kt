@file:OptIn(ExperimentalWasmJsInterop::class)

package com.pomidorka.scheduleaag.utils

import kotlinx.browser.window

internal class WasmJsVibrator : Vibrator {
    private val isDesktop: Boolean
        get() = isDesktop().toBoolean()

    override val isVibrateSupported: Boolean
        get() = checkVibrateSupport().toBoolean() && !isDesktop

    override fun vibrateClick() {
        vibrate(10)
    }

    override fun vibrate(time: Long) {
        if (isVibrateSupported && Vibrator.isEnabled) {
            window.navigator.vibrate(time.toInt())
        }
    }
}

private fun isDesktop(): JsBoolean
    = js("""
    {
        const userAgent = navigator.userAgent.toLowerCase();
        const mobileKeywords = [
            'android', 'iphone', 'ipod', 'ipad', 'windows phone', 
            'blackberry', 'webos', 'opera mini', 'mobile'
        ];
        
        return !mobileKeywords.some(keyword => userAgent.includes(keyword));
    }
    """)

private fun checkVibrateSupport(): JsBoolean
    = js("!!(navigator.vibrate || navigator.webkitVibrate || navigator.mozVibrate || navigator.msVibrate)")

actual fun getVibrator(): Vibrator = WasmJsVibrator()