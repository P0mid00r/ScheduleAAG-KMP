package com.pomidorka.scheduleaag.utils

class WasmPlatform : Platform {
    override val type = PlatformType.WEB
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun currentPlatform(): Platform = WasmPlatform()