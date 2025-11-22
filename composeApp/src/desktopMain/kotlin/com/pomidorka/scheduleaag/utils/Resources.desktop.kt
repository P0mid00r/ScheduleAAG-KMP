package com.pomidorka.scheduleaag.utils

import java.io.File
import java.util.Base64

actual fun readTextFileFromBundle(name: String, extension: String): String {
    val resourceName = "$name.$extension"
    val resourceUrl = Thread.currentThread().contextClassLoader.getResource(resourceName)
        ?: error("File $resourceName not found in resources")

    return File(resourceUrl.toURI()).readText(Charsets.UTF_8)
}

actual fun getImageBase64(fileName: String): String {
    val inputStream = Thread.currentThread()
        .contextClassLoader
        .getResourceAsStream(fileName) ?: error("Image not found in resources")

    val bytes = inputStream.readBytes()
    return Base64.getEncoder().encodeToString(bytes)
}