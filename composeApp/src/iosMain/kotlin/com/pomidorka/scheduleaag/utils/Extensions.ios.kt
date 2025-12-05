package com.pomidorka.scheduleaag.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun String.openUrl() {
    val nsUrl = NSURL(string = this)
    UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>()) { isSuccess ->
        if (isSuccess) Log.info("Extensions.kt") {
            "Открыта ссылка: $this"
        }
    }
}