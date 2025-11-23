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
    id("ru.ok.tracer") version("1.1.6")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}

val localPropertiesFile = project.rootProject.file("local.properties")
val localProperties = Properties().apply {
    load(localPropertiesFile.inputStream())
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
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
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

            implementation(project.dependencies.platform("ru.ok.tracer:tracer-platform:1.1.6"))
            implementation("ru.ok.tracer:tracer-crash-report")
            implementation("ru.ok.tracer:tracer-profiler-sampling")
            
            implementation("com.yandex.android:mobileads:7.16.1")

            implementation(libs.ktor.client.android)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonMain {
            kotlin.srcDir(generatedAppConfigDir)

            dependencies {
                api("io.github.kevinnzou:compose-webview-multiplatform:2.0.3")

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
                implementation(libs.material3)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            implementation("io.github.kevinnzou:compose-webview-multiplatform-desktop:2.0.3")
            implementation(libs.ktor.client.okhttp)
            implementation("org.slf4j:slf4j-simple:2.0.13")
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
        pluginToken = localProperties.getProperty("tracerPluginToken")
        appToken = localProperties.getProperty("tracerAppToken")
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
            value = localProperties.getProperty("rustoreVersionAppToken") ?: ""
        )

        buildConfigField(
            type = "String",
            name = "RUSTORE_KEY_ID",
            value = localProperties.getProperty("rustoreKeyId") ?: "0"
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
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.pomidorka.scheduleaag.MainKt"

        nativeDistributions {
            modules("java.base")

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            val appName = rootProject.extra["appName"].toString()
            val versionName = rootProject.extra["appVersionName"].toString().plus(".0")

            packageName = "ScheduleAAG"
            packageVersion = versionName
            vendor = "P0mid00r"
            description = "Расписание колледжа «Алтайская академия гостеприимства»"
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
        jvmArgs += listOf(
            "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.awt.X11=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt.X11=ALL-UNNAMED"
        )

        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED") // recommended but not necessary

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}