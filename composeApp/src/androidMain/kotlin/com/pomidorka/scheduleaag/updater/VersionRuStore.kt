package com.pomidorka.scheduleaag.updater

data class VersionRuStore(
    val versionId: String,
    val appName: String,
    val appType: String,
    val versionName: String,
    val versionCode: Int,
    val versionStatus: String,
    val publishType: String,
    val whatsNew: String
)
