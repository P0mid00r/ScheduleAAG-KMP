package com.pomidorka.scheduleaag.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64Encoding
import platform.Foundation.stringWithContentsOfFile
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

private fun getFilePath(name: String, extension: String): String {
    val path = NSBundle.mainBundle.pathForResource(name, ofType = extension)
        ?: error("File $name.$extension not found in bundle")
    return path
}

@OptIn(ExperimentalForeignApi::class)
actual fun readTextFileFromBundle(name: String, extension: String): String {
    val path = getFilePath(name, extension)
    val nsString = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)
        ?: error("Failed to read file $name.$extension as string")

    return nsString
}

actual fun getImageBase64(fileName: String): String {
    val image = UIImage.imageNamed(fileName.split(".").first()) ?: return ""
    val data = UIImagePNGRepresentation(image) ?: return ""
    return data.base64Encoding()
}