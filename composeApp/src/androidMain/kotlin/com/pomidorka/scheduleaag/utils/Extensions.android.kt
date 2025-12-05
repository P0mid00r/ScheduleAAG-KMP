package com.pomidorka.scheduleaag.utils

import android.content.Intent
import androidx.core.net.toUri

actual fun String.openUrl() {
    val context = AppContext.applicationContext
    val intent = Intent(Intent.ACTION_VIEW, this.toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}