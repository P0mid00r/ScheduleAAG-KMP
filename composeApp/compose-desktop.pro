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

-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.FilterType { *; }
-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.ScheduleType { *; }

-dontoptimize
-ignorewarnings