package com.pomidorka.scheduleaag.utils

import android.os.Build

class AndroidPlatform : Platform {
    override val type = PlatformType.Android
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun currentPlatform(): Platform = AndroidPlatform()