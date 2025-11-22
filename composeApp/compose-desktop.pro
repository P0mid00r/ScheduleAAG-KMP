-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory

-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.FilterType { *; }
-keepclassmembers enum com.pomidorka.scheduleaag.schedule.interactive.ScheduleType { *; }

-dontoptimize
-ignorewarnings