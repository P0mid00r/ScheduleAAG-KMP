package com.pomidorka.scheduleaag.utils

import org.w3c.xhr.XMLHttpRequest
import kotlin.io.encoding.Base64

private fun getFilePath(name: String, extension: String): String {
    return "resources/$name.$extension"
}

actual fun readTextFileFromBundle(name: String, extension: String): String {
    val path = getFilePath(name, extension)
    val xhr = XMLHttpRequest()
    xhr.open("GET", path, false)
    xhr.send()
    return xhr.responseText
}

actual fun getImageBase64(fileName: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", fileName, false)
    xhr.overrideMimeType("text/plain; charset=x-user-defined")
    xhr.send()
    val bytes = xhr.responseText.map {
        (it.code and 0xFF).toByte()
    }.toByteArray()
    return Base64.Default.encode(bytes)
}