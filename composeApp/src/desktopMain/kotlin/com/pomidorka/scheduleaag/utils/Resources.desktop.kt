package com.pomidorka.scheduleaag.utils

import java.util.*

private val resourceLoader: ClassLoader
    get() = Thread.currentThread().contextClassLoader
        ?: ClassLoader.getSystemClassLoader()

actual fun readTextFileFromBundle(name: String, extension: String): String {
    val resourceName = "$name.$extension"
    val inputStream = resourceLoader.getResourceAsStream(resourceName)
        ?: error("File $resourceName not found in resources")

    return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}

actual fun getImageBase64(fileName: String): String {
    val inputStream = resourceLoader.getResourceAsStream(fileName)
        ?: error("Image not found in resources")

    val bytes = inputStream.use { it.readBytes() }
    return Base64.getEncoder().encodeToString(bytes)
}