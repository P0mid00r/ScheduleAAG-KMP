# ------------------------------------------------------------------
# Для io.github.conamobiledev:pdfkmp
# ------------------------------------------------------------------
-keep class org.apache.pdfbox.** { *; }
-keep class org.apache.fontbox.** { *; }
-keep class org.apache.commons.logging.** { *; }

# ------------------------------------------------------------------
# Для io.github.kdroidfilter:composewebview
# ------------------------------------------------------------------
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# ------------------------------------------------------------------
# Для dev.nucleusframework:composewebview
# ------------------------------------------------------------------
-keep class dev.nucleusframework.window.tao.dispatch.** { *; }
-keep class dev.nucleusframework.window.tao.ffi.** { *; }
-keep class dev.nucleusframework.window.tao.render.** { *; }
-keep class dev.nucleusframework.webview.web.** { *; }
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }

-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.FilterType { *; }
-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.ScheduleType { *; }

-dontoptimize
-ignorewarnings