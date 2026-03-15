package com.pomidorka.scheduleaag.utils

import android.content.res.AssetManager
import java.io.InputStream
import kotlin.io.encoding.Base64

actual fun readTextFileFromBundle(name: String, extension: String): String {
    val assetManager: AssetManager = AppContext.applicationContext.assets
    val inputStream: InputStream = assetManager.open("$name.$extension")
    return String(inputStream.readBytes())
}

actual fun getImageBase64(fileName: String): String {
    val assetManager: AssetManager = AppContext.applicationContext.assets
    val inputStream: InputStream = assetManager.open(fileName)
    val bytes = inputStream.readBytes()
    return Base64.encode(bytes)
}