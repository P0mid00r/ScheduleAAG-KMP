package com.pomidorka.scheduleaag.utils

class JVMPlatform : Platform {
    override val type = PlatformType.Desktop
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun currentPlatform(): Platform = JVMPlatform()