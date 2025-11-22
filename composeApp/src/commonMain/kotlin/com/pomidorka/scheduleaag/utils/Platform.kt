package com.pomidorka.scheduleaag.utils

interface Platform {
    val type: PlatformType
    val name: String
}

enum class PlatformType {
    Desktop, Android, IOS, WEB;

    val isMobile
        get() = isAndroid || isIOS

    val isAndroid
        get() = this == Android

    val isIOS
        get() = this == IOS

    val isWeb
        get() = this == WEB

    val isNotWeb
        get() = !isWeb

    val isDesktop
        get() = this == Desktop
}

expect fun currentPlatform(): Platform