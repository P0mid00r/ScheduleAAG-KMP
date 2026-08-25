import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("ru.ok.tracer") version "1.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
}

private fun getEnv(key: String): String? {
    val envVar = System.getenv(key)
    if (!envVar.isNullOrEmpty()) return envVar

    return null
}

val localPropertiesFile = project.rootProject.file("local.properties")
val localProperties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val generatedAppConfigDir = layout.buildDirectory.dir("generated/config").get().asFile

tasks.register("generateAppConfig") {
    val appName = rootProject.extra["appName"].toString()
    val versionName = rootProject.extra["appVersionName"].toString()
    val versionCode = rootProject.extra["appVersionCode"] as Int
    val versionFile = File("$generatedAppConfigDir/AppConfig.kt")

    inputs.property("appName", appName)
    inputs.property("appVersionName", versionName)
    inputs.property("appVersionCode", versionCode)
    outputs.file(versionFile)

    doLast {
        versionFile.parentFile.mkdirs()
        versionFile.writeText(
            """
object AppConfig {
    const val APP_NAME = "$appName"
    const val VERSION_NAME = "$versionName"
    const val VERSION_CODE = $versionCode
}
            """.trimMargin()
        )
    }
}

tasks.register("configureIosXcode") {
    val versionName = rootProject.extra["appVersionName"].toString()
    val versionCode = rootProject.extra["appVersionCode"] as Int

    val configFile = projectDir.parentFile.resolve("iosApp/Configuration/Config.xcconfig")
    inputs.property("appVersionName", versionName)
    inputs.property("appVersionCode", versionCode)
    outputs.file(configFile)

    doLast {
        if (configFile.exists()) {
            val content = configFile.readText()

            val updatedContent = content
                .replace(
                    Regex("""MARKETING_VERSION\s*=\s*[^\n]*"""),
                    "MARKETING_VERSION = $versionName"
                )
                .replace(
                    Regex("""CURRENT_PROJECT_VERSION\s*=\s*[^\n]*"""),
                    "CURRENT_PROJECT_VERSION = $versionCode"
                )

            configFile.writeText(updatedContent)
            println("Updated iOS versions in Config.xcconfig:")
            println("  MARKETING_VERSION = $versionName")
            println("  CURRENT_PROJECT_VERSION = $versionCode")
        } else {
            println("Warning: iosApp/Config.xcconfig not found!")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("generateAppConfig", "configureIosXcode")
}

kotlin {
//    jvmToolchain(17)

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
//        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation("com.github.bhuvaneshw.pdfviewer:compose:1.1.0")
            implementation("com.github.bhuvaneshw.pdfviewer:compose-ui:1.1.0")

            implementation(project.dependencies.platform("ru.ok.tracer:tracer-platform:1.4.0"))
            implementation("ru.ok.tracer:tracer-crash-report")
            implementation("ru.ok.tracer:tracer-profiler-sampling")
            
            implementation("com.yandex.android:mobileads:8.3.0")

            implementation(libs.ktor.client.android)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonMain {
            kotlin.srcDir(generatedAppConfigDir)

            dependencies {
                implementation("io.github.kdroidfilter:composewebview:1.0.0-beta-02")

                implementation("com.kizitonwose.calendar:compose-multiplatform:2.9.0")
                implementation(libs.kotlinx.datetime)

                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)

                implementation(libs.ksoup)
                implementation(libs.ksoup.network)

                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.serialization)

                implementation(libs.richeditor.compose)

                implementation(libs.navigation.compose)
                implementation(libs.material.icons.extended)

                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            implementation("io.github.conamobiledev:pdfkmp:1.2.0")
            implementation("io.github.conamobiledev:pdfkmp-viewer:1.2.0")
            implementation("io.github.kdroidfilter:platformtools.appmanager:0.7.5")

            implementation(libs.ktor.client.okhttp)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

tracer {
    create("defaultConfig") {
        pluginToken = localProperties.getProperty("tracerPluginToken") ?: getEnv("ANDROID_TRACER_PLUGIN_TOKEN")!!
        appToken = localProperties.getProperty("tracerAppToken") ?: getEnv("ANDROID_TRACER_APP_TOKEN")!!
    }

    create("debug") {
        isDisabled = true
        uploadMapping = false
    }
}

android {
    namespace = "com.pomidorka.scheduleaag"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.pomidorka.scheduleaag"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = rootProject.extra["appVersionCode"] as Int
        versionName = rootProject.extra["appVersionName"].toString()

        buildConfigField(
            type = "String",
            name = "RUSTORE_API_TOKEN",
            value = localProperties.getProperty("rustoreVersionAppToken") ?: "\"${getEnv("RUSTORE_API_TOKEN")}\""
        )

        buildConfigField(
            type = "String",
            name = "RUSTORE_KEY_ID",
            value = localProperties.getProperty("rustoreKeyId") ?: "\"${getEnv("RUSTORE_KEY_ID")}\""
        )
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.pomidorka.scheduleaag.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            val appName = rootProject.extra["appName"].toString()
            val versionName = rootProject.extra["appVersionName"].toString().plus(".0")

            packageName = appName
            packageVersion = versionName
            vendor = "P0mid00r"
            description = "College schedule of the «Altai Academy of Hospitality»"
            licenseFile.set(project.rootProject.file("LICENSE.txt"))

            macOS {
                iconFile.set(project.file("icons/icon.icns"))
                bundleID = "com.pomidorka.scheduleaag"
            }
            windows {
                menu = true
                menuGroup = "start-menu-group"
                shortcut = true
                iconFile.set(project.file("icons/icon.ico"))
                perUserInstall = true
            }
            linux {
                shortcut = true
                iconFile.set(project.file("icons/icon.png"))
            }
        }

        buildTypes.release.proguard {
            isEnabled.set(true)
            obfuscate.set(true)
            configurationFiles.from("compose-desktop.pro")
        }

        jvmArgs += "--enable-native-access=ALL-UNNAMED"
    }
}