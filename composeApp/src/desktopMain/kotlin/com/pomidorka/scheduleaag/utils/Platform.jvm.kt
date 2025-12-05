package com.pomidorka.scheduleaag.utils

import org.jetbrains.skiko.hostOs

class JVMPlatform : Platform {
    override val type = PlatformType.Desktop
    override val name: String = hostOs.id
//    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun currentPlatform(): Platform = JVMPlatform()