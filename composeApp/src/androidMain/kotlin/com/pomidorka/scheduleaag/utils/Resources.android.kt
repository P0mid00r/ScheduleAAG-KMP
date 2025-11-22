package com.pomidorka.scheduleaag.utils

import android.content.res.AssetManager
import io.ktor.util.*
import java.io.InputStream

actual fun readTextFileFromBundle(name: String, extension: String): String {
    val assetManager: AssetManager = AppContext.applicationContext.assets
    val inputStream: InputStream = assetManager.open("$name.$extension")
    return String(inputStream.readBytes())
}

actual fun getImageBase64(fileName: String): String {
    val assetManager: AssetManager = AppContext.applicationContext.assets
    val inputStream: InputStream = assetManager.open(fileName)
    val bytes = inputStream.readBytes()
    return bytes.encodeBase64()
}