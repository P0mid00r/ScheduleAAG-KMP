package com.pomidorka.scheduleaag.utils

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val type = PlatformType.IOS
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun currentPlatform(): Platform = IOSPlatform()