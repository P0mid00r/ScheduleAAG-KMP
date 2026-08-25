package com.pomidorka.scheduleaag.utils

enum class OperatingSystem {
    WINDOWS, MACOS, LINUX, ANDROID, IOS, UNKNOWN
}

expect fun getOperatingSystem(): OperatingSystem