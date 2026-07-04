plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.0"
}

allprojects {
    extra["appName"] = "ScheduleAAG"
    extra["appVersionName"] = "1.41"
    extra["appVersionCode"] = 1_410
}