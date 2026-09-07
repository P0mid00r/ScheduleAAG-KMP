plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "com.pomidorka.scheduleaag"
version = "1.0.0"

application {
    mainClass.set("com.pomidorka.scheduleaag.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.serverTestHost)
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
    }
}

tasks.test {
    enabled = false
}

// Решаем проблему дубликатов при сборке дистрибутивов
tasks.distTar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.distZip {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}